package com.oo.tools.spring.boot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2025/01/08 10:22:30
 */
@Slf4j
@Configuration
public class LifeCycleBeanConfig {
    @Bean(initMethod = "initMethod")
    public LifeCycleBean lifeCycleBean() {
        log.info("启动顺序: @Bean 注解方法执行 lifeCycleBean");
        return new LifeCycleBean();
    }
    @Bean
    public LifeCycleBeanOrder2 LifeCycleBeanOrder2() {
        log.info("启动顺序: @Bean 注解方法执行 LifeCycleBeanOrder2");
        return new LifeCycleBeanOrder2();
    }
}
