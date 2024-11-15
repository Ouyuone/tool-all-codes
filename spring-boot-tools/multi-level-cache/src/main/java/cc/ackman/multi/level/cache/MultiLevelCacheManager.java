package cc.ackman.multi.level.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class MultiLevelCacheManager implements CacheManager {

    private final Map<String, Cache> cacheMap = new ConcurrentHashMap<>();
    private final RedisTemplate<String, Object> redisTemplate;
    private final long caffeineExpiration;
    private final long redisExpiration;
    private final int caffeineMaxSize;

    public MultiLevelCacheManager(RedisTemplate<String, Object> redisTemplate,
                                  long caffeineExpiration,
                                  long redisExpiration,
                                  int caffeineMaxSize) {
        this.redisTemplate = redisTemplate;
        this.caffeineExpiration = caffeineExpiration;
        this.redisExpiration = redisExpiration;
        this.caffeineMaxSize = caffeineMaxSize;
    }

    @Override
    public Cache getCache(String name) {
        return cacheMap.computeIfAbsent(name, this::createMultiLevelCache);
    }

    @Override
    public Collection<String> getCacheNames() {
        return Collections.unmodifiableSet(cacheMap.keySet());
    }

    private Cache createMultiLevelCache(String name) {
        com.github.benmanes.caffeine.cache.Cache<Object, Object> caffeineCache = Caffeine.newBuilder()
            .expireAfterWrite(caffeineExpiration, TimeUnit.SECONDS)
            .maximumSize(caffeineMaxSize)
            .build();

        return new MultiLevelCache(name, caffeineCache, redisTemplate, redisExpiration);
    }
}
