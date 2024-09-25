package cn.hippo4j.monitor.base;

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.config.springboot.starter.config.BootstrapConfigProperties;
import cn.hippo4j.config.springboot.starter.monitor.ThreadPoolMonitorExecutor;
import cn.hippo4j.config.springboot.starter.support.DynamicThreadPoolConfigService;
import cn.hippo4j.config.springboot.starter.support.DynamicThreadPoolPostProcessor;
import cn.hippo4j.core.executor.state.ThreadPoolRunStateHandler;
import cn.hippo4j.core.toolkit.inet.InetUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2024/09/25 14:50:14
 */
@Configuration
@EnableConfigurationProperties(BootstrapConfigProperties.class)
public class ThreadPoolMonitorConfig {
    
    @Bean
    public ThreadPoolRunStateHandler threadPoolRunStateHandler(InetUtils hippo4jInetUtils, ConfigurableEnvironment environment)
    {
        return new ThreadPoolRunStateHandler(hippo4jInetUtils,environment);
    }
    
    @Bean
    public ThreadPoolMonitorExecutor hippo4jDynamicThreadPoolMonitorExecutor(BootstrapConfigProperties bootstrapConfigProperties) {
        return new ThreadPoolMonitorExecutor(bootstrapConfigProperties);
    }
    
    @Bean
    @ConditionalOnMissingBean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ApplicationContextHolder hippo4jApplicationContextHolder() {
        return new ApplicationContextHolder();
    }
    
}
