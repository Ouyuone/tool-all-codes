package cc.magicjson.caller.infrastructure.autoconfigure;


import cc.magicjson.caller.domain.discovery.ServiceDiscovery;

import cc.magicjson.caller.infrastructure.discovery.SimpleServiceDiscovery;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * 服务发现的自动配置类。
 * 这个类根据配置设置适当的 ServiceDiscovery bean
 *
 * @author <a href="mailto:it_magicjson@163.com">MagicJson</a>
 * @since 1.0.0
 */
@AutoConfiguration
public class ServiceDiscoveryAutoConfiguration {

    /**
     * 如果没有其他 ServiceDiscovery bean 存在，
     * 且 service-discovery.type 属性设置为 'simple' 或未设置，
     * 则创建一个 SimpleServiceDiscovery bean。
     *
     * @return SimpleServiceDiscovery 实例
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "service-discovery.type", havingValue = "simple", matchIfMissing = true)
    public ServiceDiscovery simpleServiceDiscovery() {
        return new SimpleServiceDiscovery();
    }

    // TODO 预埋-在这里添加其他服务发现实现

//    @Bean
//    @ConditionalOnProperty(name = "service-discovery.type", havingValue = "nacos")
//    public ServiceDiscovery nacosServiceDiscovery() {
//        return new NacosServiceDiscovery();
//    }
//
//    @Bean
//    @ConditionalOnProperty(name = "service-discovery.type", havingValue = "zookeeper")
//    public ServiceDiscovery zookeeperServiceDiscovery() {
//        return new ZookeeperServiceDiscovery();
//    }

}
