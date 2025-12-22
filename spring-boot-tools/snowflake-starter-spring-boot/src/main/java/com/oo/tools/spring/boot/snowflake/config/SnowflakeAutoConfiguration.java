package com.oo.tools.spring.boot.snowflake.config;

import com.oo.tools.spring.boot.snowflake.service.SnowflakeService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Snowflake自动配置类
 *
 * @author Yu.ou
 * @desc: Snowflake自动配置
 * @since: 1.0.0
 */
@Configuration
@ConditionalOnClass({RedisTemplate.class, cn.hutool.core.lang.Snowflake.class, ObjectMapper.class})
@ConditionalOnProperty(prefix = "snowflake", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(SnowflakeProperties.class)
@EnableScheduling
public class SnowflakeAutoConfiguration {

    /**
     * Redis连接工厂配置
     * 只有在没有 RedisConnectionFactory Bean 时才创建
     *
     * @param properties Snowflake配置属性
     * @return RedisConnectionFactory
     */
    @Bean
    @ConditionalOnMissingBean(RedisConnectionFactory.class)
    public RedisConnectionFactory redisConnectionFactory(SnowflakeProperties properties) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        
        // 从配置中读取 Redis 连接信息，如果没有配置则使用默认值
        String host = properties.getRedisHost() != null ? properties.getRedisHost() : "localhost";
        int port = properties.getRedisPort() != null ? properties.getRedisPort() : 6379;
        int database = properties.getRedisDatabase() != null ? properties.getRedisDatabase() : 0;
        
        config.setHostName(host);
        config.setPort(port);
        config.setDatabase(database);
        
        if (properties.getRedisPassword() != null) {
            config.setPassword(properties.getRedisPassword());
        }
        
        LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
        factory.afterPropertiesSet();
        return factory;
    }

    /**
     * RedisTemplate配置
     * 只有在没有名为 "redisTemplate" 的 RedisTemplate Bean 时才创建
     *
     * @param connectionFactory Redis连接工厂
     * @return RedisTemplate
     */
    @Bean(name = "redisTemplate")
    @ConditionalOnMissingBean(name = "redisTemplate")
    @ConditionalOnClass(ObjectMapper.class)
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // 设置 key 序列化器
        StringRedisSerializer keySerializer = StringRedisSerializer.UTF_8;
        template.setKeySerializer(keySerializer);
        template.setHashKeySerializer(keySerializer);
        
        // 设置 value 序列化器（使用 Jackson JSON 序列化）
        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);
        
        template.afterPropertiesSet();
        return template;
    }

    /**
     * SnowflakeService配置
     *
     * @param snowflakeProperties Snowflake配置属性
     * @param redisTemplate RedisTemplate
     * @return SnowflakeService
     */
    @Bean
    @ConditionalOnMissingBean
    public SnowflakeService snowflakeService(SnowflakeProperties snowflakeProperties,
                                             RedisTemplate<String, Object> redisTemplate) {
        return new SnowflakeService(snowflakeProperties, redisTemplate);
    }
}

