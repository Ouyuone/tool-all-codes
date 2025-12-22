package com.oo.tools.spring.boot.snowflake.service;

import cn.hutool.core.lang.Snowflake;
import com.oo.tools.spring.boot.snowflake.config.SnowflakeProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.stream.Collectors;

/**
 * Snowflake ID生成服务
 *
 * @author Yu.ou
 * @desc: Snowflake ID生成服务
 * @since: 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class SnowflakeService {

    private final SnowflakeProperties snowflakeProperties;
    
    private final RedisTemplate<String, Object> redisTemplate;

    private final Map<String, SnowflakeItem> snowflakeMap = new ConcurrentHashMap<>();

    /**
     * 获取指定名称的Snowflake实例
     *
     * @param name Snowflake实例名称
     * @return Snowflake实例，如果配置不存在则返回null
     */
    public Snowflake getSnowflake(String name) {
        SnowflakeItem snowflake = this.snowflakeMap.get(name);
        if (Objects.isNull(snowflake) && Objects.nonNull(snowflakeProperties.getInstances())
                && Objects.nonNull(snowflakeProperties.getInstances().get(name))) {
            return this.snowflakeMap.computeIfAbsent(name, k -> {
                SnowflakeProperties.SnowflakeInstanceProperties properties = snowflakeProperties.getInstances().get(k);
                Long workerId = this.newWorkerId(k, properties);
                return new SnowflakeItem(workerId, new Snowflake(
                        new Date(properties.getEpochTimestamp()),
                        workerId != null ? workerId : 1L,
                        properties.getDataCenterId() != null ? properties.getDataCenterId() : 0L,
                        properties.getOptimizeGetTimestamp() != null ? properties.getOptimizeGetTimestamp() : true,
                        properties.getTimeOffset() != null ? properties.getTimeOffset() : 1000L,
                        properties.getRandomSequenceLimit() != null ? properties.getRandomSequenceLimit() : 10L));
            }).getSnowflake();
        }
        return Objects.isNull(snowflake) ? null : snowflake.getSnowflake();
    }

    /**
     * 生成新的WorkerId
     *
     * @param name       Snowflake实例名称
     * @param properties 配置属性
     * @return WorkerId
     */
    private Long newWorkerId(String name, SnowflakeProperties.SnowflakeInstanceProperties properties) {
        Long workerId = null;
        try {
            String prefix = StringUtils.isNotBlank(snowflakeProperties.getPrefix())
                    ? snowflakeProperties.getPrefix() + ":" : "";
            String key = prefix + "snowflake:" + name;
            String keyLock = key + ":lock";
            ValueOperations<String, Object> valueOperations = this.redisTemplate.opsForValue();
            try {
                // 简单锁
                int count = 0;
                boolean locked = true;
                while (!Objects.equals(Boolean.TRUE, valueOperations.setIfAbsent(keyLock, Boolean.TRUE, 10, TimeUnit.SECONDS))) {
                    if (count > 10) {
                        locked = false;
                        break;
                    } else {
                        count++;
                        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(200));
                    }
                }
                if (locked) {
                    HashOperations<String, String, Object> hashOperations = this.redisTemplate.opsForHash();
                    /* workerId - timestamp */
                    Map<String, Object> snowflakeWorkerIdMap = hashOperations.entries(key);
                    if (snowflakeWorkerIdMap == null || snowflakeWorkerIdMap.isEmpty()) {
                        workerId = 1L;
                    } else {
                        List<Long> workerIdList = snowflakeWorkerIdMap.entrySet()
                                .stream()
                                .filter(entry -> {
                                    String value = Objects.toString(entry.getValue(), "0");
                                    return NumberUtils.isCreatable(entry.getKey()) && NumberUtils.isCreatable(value)
                                            && NumberUtils.createLong(value) + properties.getWorkerTimeout() > System.currentTimeMillis();
                                })
                                .map(entry -> NumberUtils.createLong(entry.getKey()))
                                .sorted(Long::compareTo)
                                .toList();

                        if (workerIdList == null || workerIdList.isEmpty()) {
                            workerId = 1L;
                        } else {
                            Long minValue = workerIdList.get(0);
                            if (minValue > 1L) {
                                workerId = 1L;
                            } else {
                                for (long i = 1, maxValue = workerIdList.get(workerIdList.size() - 1); i < maxValue; i++) {
                                    if (!workerIdList.contains(i)) {
                                        workerId = i;
                                        break;
                                    }
                                }
                            }
                        }
                        if (Objects.isNull(workerId)) {
                            workerId = workerIdList.get(workerIdList.size() - 1) + 1;
                        }
                    }
                    hashOperations.put(key, String.valueOf(workerId), System.currentTimeMillis());
                } else {
                    throw new RuntimeException("Failed to get the snowflake lock by " + name);
                }
            } finally {
                this.redisTemplate.delete(keyLock);
            }
            return workerId;
        } catch (Exception e) {
            log.error("Failed to create workerId for snowflake: {}", name, e);
            throw new RuntimeException("Failed to create workerId for snowflake: " + name, e);
        }
    }

    /**
     * 定期续期WorkerId（每5分钟执行一次）
     */
    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void snowflakeRenewal() {
        long timestamp = System.currentTimeMillis();
        HashOperations<String, String, Object> hashOperations = this.redisTemplate.opsForHash();
        String prefix = StringUtils.isNotBlank(snowflakeProperties.getPrefix())
                ? snowflakeProperties.getPrefix() + ":" : "";
        this.snowflakeMap.keySet().forEach(key -> {
            String snowflakeKey = prefix + "snowflake:" + key;
            Map<String, Object> snowflakeWorkerIdMap = hashOperations.entries(snowflakeKey);
            if (snowflakeWorkerIdMap != null && !snowflakeWorkerIdMap.isEmpty()) {
                snowflakeWorkerIdMap.keySet().forEach(workerId -> snowflakeWorkerIdMap.put(workerId, timestamp));
                hashOperations.putAll(snowflakeKey, snowflakeWorkerIdMap);
            }
        });
    }

    /**
     * 清理资源
     */
    public void destroy() {
        String prefix = StringUtils.isNotBlank(snowflakeProperties.getPrefix())
                ? snowflakeProperties.getPrefix() + ":" : "";
        this.redisTemplate.delete(this.snowflakeMap.keySet()
                .stream()
                .map(k -> prefix + "snowflake:" + k)
                .collect(Collectors.toSet()));
        this.snowflakeMap.clear();
    }

    @Getter
    @RequiredArgsConstructor
    private static class SnowflakeItem {
        private final long workerId;
        private final Snowflake snowflake;
    }
}

