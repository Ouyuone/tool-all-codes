# Module: 多级缓存使用介绍

## 介绍
多级缓存指的是在系统中运用了多层级的缓存来构建系统，采用`本地缓存`和`远程缓存`，以提升系统的性能。
我们这次`本地缓存`采用的是**caffeine**,`远程缓存`采用的是**redis**。

## 配置CacheManager
`CacheManager`的实现类有：
- SimpleCacheManager
- ConcurrentMapCacheManager
- CaffeineCacheManager
- JCacheCacheManager
- RedisCacheManager
  默认使用`ConcurrentMapCacheManager`来作为缓存管理器。

> 在spring的配置文件中，可以配置`spring.cache.type`来指定缓存的类型。
```yaml
spring:
  cache:
    type: caffeine
```
上面的配置，指定了缓存的类型为`caffeine`，所以使用的是`CaffeineCacheManager`。

## CacheManager的作用和使用
`CacheManager`是spring-boot-starter-cache默认提供的缓存管理器，它提供了`缓存`的创建、删除、获取等操作。

```java
public interface CacheManager {

	/**
	 * Get the cache associated with the given name.
	 * <p>Note that the cache may be lazily created at runtime if the
	 * native provider supports it.
	 * @param name the cache identifier (must not be {@code null})
	 * @return the associated cache, or {@code null} if such a cache
	 * does not exist or could be not created
	 */
	@Nullable
	Cache getCache(String name);

	/**
	 * Get a collection of the cache names known by this manager.
	 * @return the names of all caches known by the cache manager
	 */
	Collection<String> getCacheNames();

}
```
`CacheManager`提供两个方法，一个是获取缓存，一个是获取缓存名称。 我们只需要重写获取缓存的方法，就可以实现缓存的创建。

>比如我们的多级缓存管理器，[MultiLevelCacheManager.java](src%2Fmain%2Fjava%2Fcc%2Fackman%2Fmulti%2Flevel%2Fcache%2FMultiLevelCacheManager.java)

## AbstractValueAdaptingCache是spring管理的缓存
`AbstractValueAdaptingCache`是spring提供的一个抽象类，它实现了`Cache`接口，并且对`Cache`接口进行了封装，使得我们可以使用`Cache`接口来操作缓存。
`AbstractValueAdaptingCache`提供了一些方法，比如`put`、`get`、`evict`等，我们可以通过这些方法来操作缓存。
`AbstractValueAdaptingCache`最重要的是`lookup`方法，它是spring提供的一个抽象方法，用于查找缓存，它用于将缓存的值转换为`Object`类型。
所以我们只要重写`lookup`方法，就可以实现缓存的创建。
```java 
  // 首先尝试从Caffeine缓存中获取
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
```
这个方法首先尝试从Caffeine缓存中获取，如果获取不到，则从Redis中获取。
多级缓存的实现思路就是，先从Caffeine缓存中获取，如果获取不到，则从Redis中获取。

## 缓存注解的使用
### `@Cacheable`
> `@Cacheable`注解用于标记一个方法，该方法将返回值缓存起来，下次调用时，直接从缓存中获取。 cacheNames属性用于指定缓存的名称，key属性用于指定缓存的键，unless属性用于指定缓存的条件。
> unless属性用于指定缓存的条件，返回的是Boolean类型，可用使用spEL表达式，result代表的是返回结果，如果返回值为null，则不缓存。
> 下面是一个例子：
```java
    @Cacheable(cacheNames = "users", key = "#id", unless = "#result == null")
    public User getUserById(Long id) {
    }
```
### `@CachePut`
> `@CachePut`注解用于标记一个方法，该方法将返回值缓存起来，下次调用时，直接从缓存中获取。
> 此方法会每次调用都进行缓存，也就是说，每次调用都会更新缓存。
> 下面是一个例子：
```java
    @CachePut(cacheNames = "users", key = "#result.id")
    public User updateUser(User user) {
    }
```
### `@CacheEvict`
> `@CacheEvict`注解用于标记一个方法，该方法将清除缓存。
> cacheNames属性用于指定缓存的名称，allEntries属性用于指定是否清除所有缓存，beforeInvocation属性用于指定是否在方法执行前清除缓存。
> 下面是一个例子：
```java
    @CacheEvict(cacheNames = "users", allEntries = true)
    public void clearAllUsers() {}
```
> beforeInvocation属性用于指定是否在方法执行前清除缓存，如果为true，则在方法执行前清除缓存，如果为false，则在方法执行后清除缓存。
```java
    @CacheEvict(cacheNames = "users", key = "#id",beforeInvocation=true)
    public void deleteUser(Long id) {}
```

## `@CacheConfig`
`@CacheConfig`注解用于标记一个类，该类中的方法将使用同一个缓存配置。
`@CacheConfig`的属性有:
- cacheNames: 指定缓存的名称，多个名称用逗号隔开。
- cacheManager: 指定缓存管理器。
- cacheResolver: 指定缓存解析器。
- keyGenerator: 指定键生成器。 默认的键生成器是`SimpleKeyGenerator`，它根据参数列表生成键。 

>下面是一个例子：
 ```java
    @Service
    @CacheConfig(cacheNames = "users")
    public class UserServiceImpl implements UserService  {
     }
```