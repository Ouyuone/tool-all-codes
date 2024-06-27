package com.oo.tools.spring.boot.config;

import com.oo.tools.spring.boot.domain.service.DynamicDataSource;
import com.oo.tools.spring.boot.support.PropertyUtil;
import com.oo.tools.spring.boot.trigger.DynamicSwitchAop;
import com.oo.tools.spring.boot.types.DynamicDatasourceLeveType;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2024/06/26 17:06:15
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.datasource",name="dynamic.enabled",havingValue = "true")
public class DynamicDatasourceConfig implements EnvironmentAware {

    private static final Logger logger = LoggerFactory.getLogger(DynamicDatasourceConfig.class);


    /**
     * 分库全局属性
     */
    private static final String TAG_GLOBAL = "global";

    /**
     * 连接池属性
     */
    private static final String TAG_POOL = "pool";

    private Map<String, Map<String, Object>> datasourceMap = new HashMap<>();

    @Bean
    public DataSource dataSource() {
        // 创建数据源
        Map<Object, Object> targetDataSources = new HashMap<>();
        try {
            for (String datasourceName : datasourceMap.keySet()) {

                Map<String, Object> datasourceProperty = datasourceMap.get(datasourceName);

                DataSourceProperties dataSourceProperties = new DataSourceProperties();
                dataSourceProperties.setUrl((String) datasourceProperty.get("url"));
                dataSourceProperties.setUsername((String) datasourceProperty.get("username"));
                dataSourceProperties.setPassword((String) datasourceProperty.get("password"));

                String driverClassName = datasourceProperty.get("driver-class-name") == null ? "com.zaxxer.hikari.HikariDataSource" : datasourceProperty.get("driver-class-name").toString();
                dataSourceProperties.setDriverClassName(driverClassName);

                String typeClassName = datasourceProperty.get("type-class-name") == null ? "com.zaxxer.hikari.HikariDataSource" : datasourceProperty.get("type-class-name").toString();

                dataSourceProperties.setDriverClassName((String) datasourceProperty.get("driver-class-name"));
                // 初始化数据源
                DataSource dataSource = dataSourceProperties.initializeDataSourceBuilder().type((Class<DataSource>) Class.forName(typeClassName)).build();

                MetaObject dsMeta = SystemMetaObject.forObject(dataSource);
                Map<String, Object> poolProps = (Map<String, Object>) (datasourceProperty.containsKey(TAG_POOL) ? datasourceProperty.get(TAG_POOL) : Collections.EMPTY_MAP);
                for (Map.Entry<String, Object> entry : poolProps.entrySet()) {
                    // 中划线转驼峰
                    String key = com.oo.tools.spring.boot.support.StringUtils.middleScoreToCamelCase(entry.getKey());
                    if (dsMeta.hasSetter(key)) {
                        dsMeta.setValue(key, entry.getValue());
                    }
                }

                targetDataSources.put(datasourceName, dataSource);
            }

//            new HikariDataSource().setMinimumIdle();

        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("can not find datasource type class by class name", e);
        }

        // 设置数据源
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setTargetDataSources(targetDataSources);
        // master为默认数据源
        dynamicDataSource.setDefaultTargetDataSource(targetDataSources.get(DynamicDatasourceLeveType.MASTER.getCode()));

        return dynamicDataSource;

    }

    @Bean
    @ConditionalOnBean(DataSource.class)
    public DynamicSwitchAop dynamicSwitchAop(){
        return new DynamicSwitchAop();
    }

    @Override
    public void setEnvironment(Environment environment) {

        String prefix = "spring.datasource.";

        //获取全局配置
        Map<String, Object> globalProperty = globalProperty(environment, prefix);

        // 获取配置文件中的数据源信息
        //获取主数据源配置
        try {
            Map<String, Object> masterProperty = PropertyUtil.handle(environment, prefix + DynamicDatasourceLeveType.MASTER.getCode(), Map.class);
            injectGlobal(masterProperty, globalProperty);
            datasourceMap.put(DynamicDatasourceLeveType.MASTER.getCode(), masterProperty);
        } catch (Exception e) {
            throw new RuntimeException("master data source property not found");
        }

        // 获取配置slaveList的子数据源信息
        String slaveList = environment.getProperty(prefix + "slaveList");

        //如果没配置slaveList，就寻找默认的slave配置
        if (StringUtils.isBlank(slaveList)) {
            try {
                Map<String, Object> slaveProperty = PropertyUtil.handle(environment, prefix + DynamicDatasourceLeveType.SLAVE.getCode(), Map.class);
                injectGlobal(slaveProperty, globalProperty);
                datasourceMap.put(DynamicDatasourceLeveType.SLAVE.getCode(), slaveProperty);
            } catch (Exception e) {
                logger.warn("slave data source property not found");
            }
            return;
        }

        //如果配置了slaveList，就寻找配置的slave配置
        for (String slaveName : slaveList.split(",")) {
            try {
                Map<String, Object> slaveProperty = PropertyUtil.handle(environment, prefix + slaveName, Map.class);
                injectGlobal(slaveProperty, globalProperty);
                datasourceMap.put(slaveName, slaveProperty);
            } catch (Exception e) {
                throw new RuntimeException(String.format("slaveName:{%s} data source property not found", slaveName));
            }
        }

        // 初始化数据源
        // 设置数据源到DynamicDataSource中
    }


    private static Map<String, Object> globalProperty(Environment environment, String prefix) {
        try {
            return PropertyUtil.handle(environment, prefix + TAG_GLOBAL, Map.class);
        } catch (Exception e) {
            return Collections.EMPTY_MAP;
        }
    }


    private void injectGlobal(Map<String, Object> origin, Map<String, Object> global) {
        for (String key : global.keySet()) {
            if (!origin.containsKey(key)) {
                origin.put(key, global.get(key));
            } else if (origin.get(key) instanceof Map) {
                injectGlobal((Map<String, Object>) origin.get(key), (Map<String, Object>) global.get(key));
            }
        }
    }
}
