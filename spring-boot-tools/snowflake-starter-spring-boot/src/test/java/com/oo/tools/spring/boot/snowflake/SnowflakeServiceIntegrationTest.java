package com.oo.tools.spring.boot.snowflake;

import cn.hutool.core.lang.Snowflake;
import com.oo.tools.spring.boot.snowflake.service.SnowflakeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Snowflake Service 集成测试
 * 
 * 注意：此测试需要 Redis 服务运行在 localhost:6379
 * 如果没有 Redis，可以使用 Docker 启动：
 * docker run -d -p 6379:6379 redis:latest
 *
 * @author Yu.ou
 * @desc: 集成测试验证 Snowflake 功能
 * @since: 1.0.0
 */
@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test")
@DisplayName("Snowflake Service 集成测试")
public class SnowflakeServiceIntegrationTest {

    @Autowired
    private SnowflakeService snowflakeService;

    @Test
    @DisplayName("验证基本功能：生成 ID")
    public void testBasicIdGeneration() {
        Snowflake snowflake = snowflakeService.getSnowflake("device");
        assertThat(snowflake).isNotNull();

        Long id = snowflake.nextId();
        assertThat(id).isNotNull();
        assertThat(id).isPositive();

        String idStr = String.valueOf(id);
        assertThat(idStr.length()).isBetween(18, 19);

        System.out.println("✅ 生成的 ID: " + id + " (长度: " + idStr.length() + ")");
    }
    @Test
    @DisplayName("验证 ID 唯一性：连续生成1000个ID")
    public void testIdUniqueness() {
        Snowflake snowflake = snowflakeService.getSnowflake("device");
        assertThat(snowflake).isNotNull();

        Set<Long> ids = new HashSet<>();
        int count = 1000;

        for (int i = 0; i < count; i++) {
            Long id = snowflake.nextId();
            assertThat(ids).doesNotContain(id);
            ids.add(id);
        }

        assertThat(ids.size()).isEqualTo(count);
        System.out.println("✅ 成功生成 " + count + " 个唯一 ID");
    }

    @Test
    @DisplayName("验证多实例：不同实例生成不同的 ID")
    public void testMultipleInstances() {
        Snowflake deviceSnowflake = snowflakeService.getSnowflake("device");
        Snowflake userSnowflake = snowflakeService.getSnowflake("user");

        assertThat(deviceSnowflake).isNotNull();
        assertThat(userSnowflake).isNotNull();

        Long deviceId = deviceSnowflake.nextId();
        Long userId = userSnowflake.nextId();

        assertThat(deviceId).isNotEqualTo(userId);
        System.out.println("✅ Device ID: " + deviceId);
        System.out.println("✅ User ID: " + userId);
    }

    @Test
    @DisplayName("验证并发安全性：多线程生成 ID")
    public void testConcurrentIdGeneration() throws Exception {
        Snowflake snowflake = snowflakeService.getSnowflake("device");
        assertThat(snowflake).isNotNull();

        int threadCount = 10;
        int idsPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        Set<Long> allIds = new HashSet<>();
        CompletableFuture<?>[] futures = IntStream.range(0, threadCount)
                .mapToObj(i -> CompletableFuture.runAsync(() -> {
                    for (int j = 0; j < idsPerThread; j++) {
                        Long id = snowflake.nextId();
                        synchronized (allIds) {
                            assertThat(allIds).doesNotContain(id);
                            allIds.add(id);
                        }
                    }
                }, executor))
                .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(futures).get();
        executor.shutdown();

        assertThat(allIds.size()).isEqualTo(threadCount * idsPerThread);
        System.out.println("✅ 并发测试通过：生成了 " + allIds.size() + " 个唯一 ID");
    }

    @Test
    @DisplayName("验证 ID 递增性：同一实例生成的 ID 应该递增")
    public void testIdIncrement() {
        Snowflake snowflake = snowflakeService.getSnowflake("device");
        assertThat(snowflake).isNotNull();

        Long id1 = snowflake.nextId();
        Long id2 = snowflake.nextId();
        Long id3 = snowflake.nextId();

        assertThat(id2).isGreaterThan(id1);
        assertThat(id3).isGreaterThan(id2);

        System.out.println("✅ ID1: " + id1);
        System.out.println("✅ ID2: " + id2);
        System.out.println("✅ ID3: " + id3);
    }
}

