package com.oo.tools.spring.boot.config;

import com.oo.tools.spring.boot.domain.service.DynamicShardingRouterDataSource;
import com.oo.tools.spring.boot.domain.service.impl.DefaultDBRouterStrategy;
import com.oo.tools.spring.boot.supports.DBRouterConfig;
import com.oo.tools.spring.boot.supports.PropertyUtil;
import com.oo.tools.spring.boot.supports.aop.DynamicShardingRouterAop;
import com.oo.tools.spring.boot.types.DynamicDatasourceLeveType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 动态数据源+分库路由
 *
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2024/06/27 15:16:19
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.datasource", name = {"dynamic-dbs", "sharding-router"}, havingValue = "true")
public class DynamicDbsShardingRouterConfig implements EnvironmentAware {

    private static final Logger logger = LoggerFactory.getLogger(DynamicDbsShardingRouterConfig.class);


    /**
     * 分库全局属性
     */
    private static final String TAG_GLOBAL = "global";

    /**
     * 连接池属性
     */
    private static final String TAG_POOL = "pool";

    /**
     * 多数据源是否需要分片
     */
    private static Map<String, Boolean> shardingRouterMap = new HashMap<>();

    /**
     * 多数据源加分片数据源 第一级key是多数据源的名称 第二级key是分片数据源的名称 第二级的value是分片数据源的配置
     */
    private Map<String, Map<String, Map<String, Object>>> datasourceMap = new HashMap<>();

    /**
     * 默认数据源名称
     */
    private String defaultDatasourceName = "master";

    /**
     * 动态数据源的路由配置 key是动态数据源的名称 value是动态数据源的配置
     */
    private Map<String, DBRouterConfig> dbRouterConfigMap = new HashMap<>();


    @Bean
    public Map<String, DBRouterConfig> routerConfig() {
        return dbRouterConfigMap;
    }


    @Bean
    public DynamicShardingRouterAop dynamicShardingRouterAop() {
        return new DynamicShardingRouterAop(routerConfig(),defaultDBRouterStrategy());
    }

    @Bean
    public DefaultDBRouterStrategy defaultDBRouterStrategy() {
        return new DefaultDBRouterStrategy(routerConfig());
    }

    @Bean
    public DataSource dataSource() {
        // 创建数据源
        Map<Object, Object> targetDataSources = new HashMap<>();
        for (String dynamicDBName : datasourceMap.keySet()) {
            Map<String, Map<String, Object>> stringMapMap = datasourceMap.get(dynamicDBName);
            Pair<String, DataSource> dataSourcePair = doCreateDatasource(dynamicDBName, stringMapMap);
            targetDataSources.put(dataSourcePair.getLeft(), dataSourcePair.getRight());
        }

        DynamicShardingRouterDataSource shardingRouterService = new DynamicShardingRouterDataSource();
        shardingRouterService.setDefaultTargetDataSource(targetDataSources.get(defaultDatasourceName));
        shardingRouterService.setTargetDataSources(targetDataSources);

        return shardingRouterService;
    }

    private static Pair<String, DataSource> doCreateDatasource(String dynamicDBName, Map<String, Map<String, Object>> stringMapMap) {
        try {
            for (String shardingDBName : stringMapMap.keySet()) {
                Map<String, Object> shardingDBMap = stringMapMap.get(shardingDBName);
                DataSourceProperties dataSourceProperties = new DataSourceProperties();
                dataSourceProperties.setUrl(shardingDBMap.get("url").toString());
                dataSourceProperties.setUsername(shardingDBMap.get("username").toString());
                dataSourceProperties.setPassword(shardingDBMap.get("password").toString());

                String driverClassName = shardingDBMap.get("driver-class-name") == null ? "com.zaxxer.hikari.HikariDataSource" : shardingDBMap.get("driver-class-name").toString();
                dataSourceProperties.setDriverClassName(driverClassName);

                String driverCLassType = shardingDBMap.get("driver-class-type") == null ? "com.zaxxer.hikari.HikariDataSource" : shardingDBMap.get("driver-class-type").toString();

                DataSource dataSource = dataSourceProperties.initializeDataSourceBuilder().type((Class<? extends DataSource>) Class.forName(driverCLassType)).build();

                MetaObject metaObject = SystemMetaObject.forObject(dataSource);

                Map<String, Object> poolProps = (Map<String, Object>) (shardingDBMap.containsKey(TAG_POOL) ? shardingDBMap.get(TAG_POOL) : Collections.EMPTY_MAP);

                for (String poolPropKey : poolProps.keySet()) {

                    String poolProp = com.oo.tools.spring.boot.supports.StringUtils.middleScoreToCamelCase(poolPropKey);
                    if (metaObject.hasSetter(poolProp)) {
                        metaObject.setValue(poolProp, poolProps.get(poolPropKey));
                    }
                }
                return Pair.of(dynamicDBName + "." + shardingDBName, dataSource);

            }
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("can not find datasource type class by class name", e);
        }
        return null;
    }


    @Override
    public void setEnvironment(Environment environment) {
        String prefix = "spring.datasource.";

        Map<String, Object> globalProperty = getGlobalProperty(environment, prefix);


        masterDBProperty(environment, prefix, globalProperty);


        slaveDBProperty(environment, prefix, globalProperty);

        ((ConfigurableEnvironment) environment).getPropertySources().addLast(new MapPropertySource("defaultDataSource", Map.of("defaultDataSource", defaultDatasourceName)));
    }

    private void slaveDBProperty(Environment environment, String prefix, Map<String, Object> globalProperty) {
        String slaveList = environment.getProperty(prefix + "slaveList");

        if (StringUtils.isNotBlank(slaveList)) {
            for (String slaveName : slaveList.split(",")) {
                Map<String, Object> salveProperty;
                try {
                    salveProperty = PropertyUtil.handle(environment, prefix + slaveName, Map.class);
                } catch (Exception e) {
                    throw new RuntimeException(String.format("slaveName:{%s} data source property not found", slaveName));
                }

                setDBRouterConfig(environment, slaveName, salveProperty);

                if (salveProperty.get("sharding-router") instanceof Boolean) {
                    shardingRouterMap.put(slaveName, true);
                    setDefaultDB(environment, salveProperty, prefix, slaveName, globalProperty);
                    if (salveProperty.get("list") instanceof String list) {

                        loopSetShardingDB(environment, slaveName, list, prefix, globalProperty);
                        continue;
                    }
                    throw new RuntimeException(String.format("slaveName:{%s} list property not found", slaveName));
                } else {
                    shardingRouterMap.put(slaveName, false);
                    injectGlobal(salveProperty, globalProperty);
                    datasourceMap.computeIfAbsent(slaveName, k -> new HashMap<>()).put(slaveName, salveProperty);
                }

            }
        } else {

            String slaveName = DynamicDatasourceLeveType.SLAVE.getCode();
            Map<String, Object> salveProperty;
            try {
                salveProperty = PropertyUtil.handle(environment, prefix + DynamicDatasourceLeveType.SLAVE.getCode(), Map.class);
            } catch (Exception e) {
                throw new RuntimeException(String.format("slaveName:{%s} data source property not found", slaveName));
            }
            setDBRouterConfig(environment, slaveName, salveProperty);

            if (salveProperty.get("sharding-router") instanceof Boolean) {
                shardingRouterMap.put(slaveName, true);
                setDefaultDB(environment, salveProperty, prefix, slaveName, globalProperty);
                if (salveProperty.get("list") instanceof String list) {

                    loopSetShardingDB(environment, slaveName, list, prefix, globalProperty);
                }
                throw new RuntimeException(String.format("slaveName:{%s} list property not found", slaveName));
            } else {
                shardingRouterMap.put(slaveName, false);
                injectGlobal(salveProperty, globalProperty);
                datasourceMap.computeIfAbsent(slaveName, k -> new HashMap<>()).put(slaveName, salveProperty);
            }

        }
    }

    private void masterDBProperty(Environment environment, String prefix, Map<String, Object> globalProperty) {
        Map<String, Object> masterProperty = PropertyUtil.handle(environment, prefix + DynamicDatasourceLeveType.MASTER.getCode(), Map.class);

        setDBRouterConfig(environment, DynamicDatasourceLeveType.MASTER.getCode(), masterProperty);

        if (masterProperty.get("sharding-router") instanceof Boolean) {
            shardingRouterMap.put(DynamicDatasourceLeveType.MASTER.getCode(), true);
            setDefaultDB(environment, masterProperty, prefix, DynamicDatasourceLeveType.MASTER.getCode(), globalProperty, true);
            if (masterProperty.get("list") instanceof String list) {

                loopSetShardingDB(environment, DynamicDatasourceLeveType.MASTER.getCode(), list, prefix, globalProperty);
                return;
            }
            throw new RuntimeException(String.format("masterName:{%s} list property not found", DynamicDatasourceLeveType.MASTER.getCode()));
        } else {
            shardingRouterMap.put(DynamicDatasourceLeveType.MASTER.getCode(), false);
            injectGlobal(masterProperty, globalProperty);
            datasourceMap.computeIfAbsent(DynamicDatasourceLeveType.MASTER.getCode(), k -> new HashMap<>()).put(DynamicDatasourceLeveType.MASTER.getCode(), masterProperty);
        }
    }

    private void setDBRouterConfig(Environment environment, String dynamicDBName, Map<String, Object> masterProperty) {
        int dbCount = Integer.parseInt(Objects.requireNonNull(masterProperty.getOrDefault("dbCount", 1)).toString());
        int tbCount = Integer.parseInt(Objects.requireNonNull(masterProperty.getOrDefault("tbCount", 1)).toString());
        String routerKey = Objects.requireNonNull(masterProperty.getOrDefault("routerKey", "id")).toString();
        dbRouterConfigMap.put(dynamicDBName, new DBRouterConfig(dbCount, tbCount, routerKey));
    }

    /**
     * 循环查找分片数据源配置
     *
     * @param environment
     * @param slaveName
     * @param list
     * @param prefix
     * @param globalProperty
     */
    private void loopSetShardingDB(Environment environment, String slaveName, String list, String prefix, Map<String, Object> globalProperty) {
        for (String shardingDbName : list.split(",")) {
            try {
                Map<String, Object> dbProperty = PropertyUtil.handle(environment, prefix + slaveName + "." + shardingDbName, Map.class);
                injectGlobal(dbProperty, globalProperty);
                datasourceMap.computeIfAbsent(slaveName, k -> new HashMap<>()).put(shardingDbName, dbProperty);
            } catch (Exception e) {
                throw new RuntimeException(String.format("slaveName:{%s} shardingDbName :{%s} property not found", slaveName, shardingDbName));
            }
        }
    }

    private void setDefaultDB(Environment environment, Map<String, Object> shardingBDProperty, String prefix, String dynamicDBName, Map<String, Object> globalProperty) {
        setDefaultDB(environment, shardingBDProperty, prefix, dynamicDBName, globalProperty, false);
    }

    private void setDefaultDB(Environment environment, Map<String, Object> shardingBDProperty, String prefix, String dynamicDBName, Map<String, Object> globalProperty, Boolean setDefaultDatabaseName) {
        if (shardingBDProperty.get("default") instanceof String defaultDb) {
            try {
                Map<String, Object> defaultDBProperty = PropertyUtil.handle(environment, prefix + dynamicDBName + "." + defaultDb, Map.class);
                injectGlobal(defaultDBProperty, globalProperty);
                datasourceMap.computeIfAbsent(dynamicDBName, k -> new HashMap<>()).put(defaultDb, defaultDBProperty);
            } catch (Exception e) {
                throw new RuntimeException(String.format("dynamicDBName:{%s} default db:{%s} property not found", dynamicDBName, defaultDb));
            }
            if (setDefaultDatabaseName) defaultDatasourceName = dynamicDBName + "." + defaultDb;
        } else {
            throw new RuntimeException(String.format("dynamicDBName:{%s} default property not exist", dynamicDBName));
        }
    }

    private Map<String, Object> getGlobalProperty(Environment environment, String prefix) {
        try {
            return PropertyUtil.handle(environment, prefix + TAG_GLOBAL, Map.class);
        } catch (Exception e) {
            return Collections.EMPTY_MAP;
        }
    }

    private void injectGlobal(Map<String, Object> defaultDBProperty, Map<String, Object> globalProperty) {
        for (String globalKey : globalProperty.keySet()) {
            if (!defaultDBProperty.containsKey(globalKey)) {
                defaultDBProperty.put(globalKey, globalProperty.get(globalKey));
            } else {
                injectGlobal((Map<String, Object>) defaultDBProperty.get(globalKey), (Map<String, Object>) globalProperty.get(globalKey));
            }
        }
    }
}
