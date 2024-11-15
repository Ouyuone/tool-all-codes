package cc.ackman.multi.level.cache;

import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.cache.support.AbstractValueAdaptingCache;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class MultiLevelCache extends AbstractValueAdaptingCache {

    private final String name;
    private final Cache<Object, Object> caffeineCache;
    private final RedisTemplate<String, Object> redisTemplate;
    private final long redisExpiration;

    public MultiLevelCache(String name, Cache<Object, Object> caffeineCache,
                           RedisTemplate<String, Object> redisTemplate, long redisExpiration) {
        super(true); // 允许缓存null值
        this.name = name;
        this.caffeineCache = caffeineCache;
        this.redisTemplate = redisTemplate;
        this.redisExpiration = redisExpiration;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    @Override
    protected Object lookup(Object key) {
        // 首先尝试从Caffeine缓存中获取
        Object value = caffeineCache.getIfPresent(key);
        if (value != null) {
            return value;
        }

        // 如果Caffeine中没有，则从Redis中获取
        String redisKey = createRedisKey(key);
        value = redisTemplate.opsForValue().get(redisKey);
        if (value != null) {
            // 将从Redis获取的值放入Caffeine缓存
            caffeineCache.put(key, value);
        }
        return value;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        Object value = lookup(key);
        if (value != null) {
            return (T) value;
        }

        // 如果缓存中没有，则调用valueLoader获取值
        try {
            T newValue = valueLoader.call();
            put(key, newValue);
            return newValue;
        } catch (Exception e) {
            throw new ValueRetrievalException(key, valueLoader, e);
        }
    }

    @Override
    public void put(Object key, Object value) {
        // 同时更新Caffeine和Redis缓存
        caffeineCache.put(key, value);
        String redisKey = createRedisKey(key);
        redisTemplate.opsForValue().set(redisKey, value, redisExpiration, TimeUnit.SECONDS);
    }

    @Override
    public void evict(Object key) {
        // 同时从Caffeine和Redis中移除
        caffeineCache.invalidate(key);
        String redisKey = createRedisKey(key);
        redisTemplate.delete(redisKey);
    }

    @Override
    public void clear() {
        // 清除所有缓存
        caffeineCache.invalidateAll();
        redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys(name + ":*")));
    }

    private String createRedisKey(Object key) {
        return name + ":" + key.toString();
    }
}
