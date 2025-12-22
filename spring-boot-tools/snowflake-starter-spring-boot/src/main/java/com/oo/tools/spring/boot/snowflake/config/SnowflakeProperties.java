package com.oo.tools.spring.boot.snowflake.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Snowflake配置属性
 *
 * @author Yu.ou
 * @desc: Snowflake配置属性
 * @since: 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties("snowflake")
public class SnowflakeProperties {

    /**
     * 默认起始时间戳：2023-01-01 00:00:00 GMT
     */
    public static final long DEFAULT_EPOCH_TIMESTAMP = 1672531200000L;

    /**
     * 是否启用Snowflake
     */
    private boolean enabled = false;

    /**
     * Redis key前缀，默认为空
     */
    private String prefix = "";

    /**
     * Redis 主机地址（可选，默认：localhost）
     * 只有在没有 RedisConnectionFactory Bean 时才会使用此配置
     */
    private String redisHost = "localhost";

    /**
     * Redis 端口（可选，默认：6379）
     * 只有在没有 RedisConnectionFactory Bean 时才会使用此配置
     */
    private Integer redisPort = 6379;

    /**
     * Redis 数据库索引（可选，默认：0）
     * 只有在没有 RedisConnectionFactory Bean 时才会使用此配置
     */
    private Integer redisDatabase = 0;

    /**
     * Redis 密码（可选）
     * 只有在没有 RedisConnectionFactory Bean 时才会使用此配置
     */
    private String redisPassword;

    /**
     * Snowflake配置属性映射
     */
    private Map<String, SnowflakeInstanceProperties> instances;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SnowflakeInstanceProperties {
        /**
         * 数据中心标识
         */
        private Long dataCenterId;

        /**
         * 初始化时间起点（毫秒时间戳）
         */
        private Long epochTimestamp = DEFAULT_EPOCH_TIMESTAMP;

        /**
         * 优化获取时间戳性能
         */
        private Boolean optimizeGetTimestamp = true;

        /**
         * 允许时间回拨的毫秒数(默认：1000，1秒)
         */
        private Long timeOffset = 1000L;

        /**
         * 当在低频模式下时，序号始终为0，导致生成ID始终为偶数<br/>
         * 此属性用于限定一个随机上限，在不同毫秒下生成序号时，给定一个随机数，避免偶数问题。<br/>
         * 注意次数必须小于SEQUENCE_MASK，0表示不使用随机数。<br/>
         * 这个上限不包括值本身
         */
        private Long randomSequenceLimit = 10L;

        /**
         * Worker节点超时时间，单位：毫秒
         * 默认：10分钟 + 随机1-2秒
         */
        private Long workerTimeout = TimeUnit.MINUTES.toMillis(10) + ThreadLocalRandom.current().nextLong(1000, 2000);

        public void setWorkerTimeout(Long workerTimeout) {
            this.workerTimeout = workerTimeout + ThreadLocalRandom.current().nextLong(1000, 2000);
        }
    }
}

