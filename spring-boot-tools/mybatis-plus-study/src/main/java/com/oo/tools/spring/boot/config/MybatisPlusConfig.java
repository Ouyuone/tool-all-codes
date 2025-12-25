package com.oo.tools.spring.boot.config;


import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.oo.tools.spring.boot.meta.FiledMetaObjectHandler;
import com.oo.tools.spring.boot.typehandler.EnumToCodeForStringTypeHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@MapperScan("com.oo.tools.spring.boot.repository")
public class MybatisPlusConfig {
    
    private final MybatisPlusProperties properties;
    
    
    public MybatisPlusConfig(
            MybatisPlusProperties properties) {
        this.properties = properties;
    }
    
    /**
     * MyBatis-Plus拦截器配置
     * 添加租户和分页拦截器
     * 优先级：租户 > 分页 > 乐观锁 > 字段填充
     *
     * @return {@link MybatisPlusInterceptor}
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        
        // 添加租户拦截器
        TenantLineInnerInterceptor tenantInterceptor = new TenantLineInnerInterceptor();
        tenantInterceptor.setTenantLineHandler(new TenantLineHandler());
        interceptor.addInnerInterceptor(tenantInterceptor);
        
        // 添加分页拦截器
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor();
        paginationInterceptor.setDbType(DbType.MYSQL);
        interceptor.addInnerInterceptor(paginationInterceptor);
        
        // 添加自动填充拦截器（这里虽然没有显式添加填充拦截器，但可以通过MetaObjectHandler实现字段填充）
        
        return interceptor;
    }
    
    
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new FiledMetaObjectHandler();
    }
    
    /**
     * 自定义sql 注入器
     *
     * @return
     */
    @Bean
    public DefaultSqlInjector defaultSqlInjector() {
        return new BatchMethodSqlInjector();
    }
    
    /**
     * 自定义sqlFactory
     *
     * @param dataSource
     * @return
     * @throws Exception
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource, MybatisPlusInterceptor mybatisPlusInterceptor, MetaObjectHandler metaObjectHandler) throws Exception {
        final MybatisSqlSessionFactoryBean sqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        
        
        if (StringUtils.isNotBlank(properties.getConfigLocation())) {
            sqlSessionFactoryBean.setConfigLocation(new PathMatchingResourcePatternResolver().getResources(properties.getConfigLocation())[0]);
        } else {
            // 配置MybatisConfiguration
            MybatisConfiguration configuration = new MybatisConfiguration();
            // 设置元对象处理器到配置中
            sqlSessionFactoryBean.setConfiguration(configuration);
        }
        
        // 设置映射文件位置和类型别名包
        sqlSessionFactoryBean.setMapperLocations(properties.resolveMapperLocations());
        sqlSessionFactoryBean.setTypeAliasesPackage(properties.getTypeAliasesPackage());
        sqlSessionFactoryBean.setTransactionFactory(new SpringManagedTransactionFactory());
        
        // 字段自动填充配置
        GlobalConfig globalConfig = properties.getGlobalConfig();
        globalConfig.setMetaObjectHandler(metaObjectHandler);
        globalConfig.setSqlInjector(defaultSqlInjector());
        sqlSessionFactoryBean.setGlobalConfig(globalConfig);
        
        
        // 添加插件（最关键的一步，将租户插件设置到SqlSessionFactory）
        sqlSessionFactoryBean.setPlugins(mybatisPlusInterceptor);
        // 自定义枚举转换器
        sqlSessionFactoryBean.setDefaultEnumTypeHandler(EnumToCodeForStringTypeHandler.class);
        
        return sqlSessionFactoryBean.getObject();
    }
    
} 