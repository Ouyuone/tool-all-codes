# Snowflake Starter for Spring Boot

基于 Hutool 的 Snowflake 算法的 Spring Boot Starter，支持分布式环境下的唯一ID生成。

## 功能特性

- ✅ 基于 Snowflake 算法生成唯一ID
- ✅ 支持多实例配置（可为不同业务配置不同的 Snowflake 实例）
- ✅ 自动 WorkerId 分配和管理（基于 Redis）
- ✅ 自动续期机制（防止 WorkerId 过期）
- ✅ 支持自定义起始时间戳
- ✅ 支持时间回拨容错
- ✅ 开箱即用，零配置启动

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.oo.tools.spring.boot</groupId>
    <artifactId>snowflake-starter-spring-boot</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 2. 配置 Redis（可选）

Snowflake Starter 会自动配置 Redis，但如果你已经有 Redis 配置，Starter 会使用你现有的配置。

**方式一：使用 Starter 自动配置的 Redis（推荐用于快速开始）**

Starter 会自动创建 Redis 连接，默认连接到 `localhost:6379`。

**方式二：使用项目已有的 Redis 配置（推荐用于生产环境）**

如果你已经有 Redis 配置，Starter 会自动使用你现有的 `RedisConnectionFactory` 和 `RedisTemplate` Bean。

### 3. 配置文件

在 `application.yml` 中添加配置：

```yaml
snowflake:
  enabled: true  # 启用 Snowflake
  prefix: "app"  # Redis key前缀（可选，默认为空）
  # Redis 配置（可选，只有在没有 RedisConnectionFactory Bean 时才会使用）
  redis-host: localhost  # Redis 主机地址（可选，默认：localhost）
  redis-port: 6379  # Redis 端口（可选，默认：6379）
  redis-database: 0  # Redis 数据库索引（可选，默认：0）
  redis-password: # Redis 密码（可选）
  instances:
    device:  # Snowflake 实例名称（自定义）
      data-center-id: 0  # 数据中心ID，0:新加坡，1:澳大利亚，2:欧洲(德国)
      epoch-timestamp: 1672531200000  # 起始时间戳（可选，默认2023-01-01）
      optimize-get-timestamp: true  # 优化时间戳获取性能（可选）
      time-offset: 1000  # 允许时间回拨的毫秒数（可选，默认1000）
      random-sequence-limit: 10  # 随机序列号上限（可选，默认10）
    user:  # 可以配置多个实例
      data-center-id: 0
```

### 4. 使用示例

```java
@Service
@RequiredArgsConstructor
public class DeviceService {
    
    private final SnowflakeService snowflakeService;
    
    public void createDevice() {
        // 获取 Snowflake 实例
        Snowflake snowflake = snowflakeService.getSnowflake("device");
        
        // 生成ID（返回 Long 类型，18或19位）
        Long id = snowflake.nextId();
        
        // ID 转换为字符串（18或19位）
        String idStr = String.valueOf(id);
        
        // 使用ID
        Device device = new Device();
        device.setId(id);
        // ... 保存到数据库
    }
}
```

## 配置说明

### 全局配置

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `snowflake.enabled` | boolean | `false` | 是否启用 Snowflake |
| `snowflake.prefix` | String | `""` | Redis key前缀，用于区分不同应用 |
| `snowflake.redis-host` | String | `"localhost"` | Redis 主机地址（只有在没有 RedisConnectionFactory Bean 时才会使用） |
| `snowflake.redis-port` | Integer | `6379` | Redis 端口（只有在没有 RedisConnectionFactory Bean 时才会使用） |
| `snowflake.redis-database` | Integer | `0` | Redis 数据库索引（只有在没有 RedisConnectionFactory Bean 时才会使用） |
| `snowflake.redis-password` | String | - | Redis 密码（可选，只有在没有 RedisConnectionFactory Bean 时才会使用） |

### 实例配置（instances下的配置）

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `data-center-id` | Long | - | 数据中心标识，必填 |
| `epoch-timestamp` | Long | `1672531200000` | 起始时间戳（2023-01-01） |
| `optimize-get-timestamp` | Boolean | `true` | 优化时间戳获取性能 |
| `time-offset` | Long | `1000` | 允许时间回拨的毫秒数 |
| `random-sequence-limit` | Long | `10` | 随机序列号上限，避免偶数问题 |
| `worker-timeout` | Long | `10分钟+随机1-2秒` | Worker节点超时时间 |

## ID 格式说明

Snowflake ID 是一个 64 位的长整型，结构如下：

```
64位 = 1位符号位 + 41位时间戳 + 10位机器ID + 12位序列号
```

- **符号位**：1 位（固定为 0）
- **时间戳**：41 位（毫秒级时间戳，相对于起始时间）
- **机器ID**：10 位（5 位数据中心ID + 5 位机器ID）
- **序列号**：12 位（同一毫秒内的序列号）

生成的 ID 转换为字符串后：
- **18 位**：ID 值 < 10^18
- **19 位**：ID 值 ≥ 10^18

## 工作原理

1. **WorkerId 分配**：首次使用时，通过 Redis 分布式锁分配唯一的 WorkerId
2. **WorkerId 续期**：每 5 分钟自动续期，防止 WorkerId 过期
3. **WorkerId 回收**：应用关闭时自动清理 Redis 中的 WorkerId

## 注意事项

1. **Redis 依赖**：必须配置 Redis，用于 WorkerId 管理
   - 如果项目中已有 Redis 配置，Starter 会自动使用现有的 Redis Bean
   - 如果没有 Redis 配置，Starter 会自动创建默认的 Redis 连接（localhost:6379）
2. **配置匹配**：`getSnowflake(name)` 中的 `name` 必须与配置中的 key 一致
3. **唯一性保证**：同一毫秒内同一机器最多生成 4096 个 ID（2^12）
4. **时间同步**：建议服务器时间同步，避免时间回拨问题
5. **Bean 优先级**：如果项目中已定义了 `RedisConnectionFactory` 或 `RedisTemplate` Bean，Starter 会优先使用项目中的配置

## 许可证

MIT License

