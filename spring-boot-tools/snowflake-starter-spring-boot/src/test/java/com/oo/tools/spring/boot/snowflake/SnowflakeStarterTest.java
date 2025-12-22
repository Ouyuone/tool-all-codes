package com.oo.tools.spring.boot.snowflake;

import cn.hutool.core.lang.Snowflake;
import com.oo.tools.spring.boot.snowflake.config.SnowflakeAutoConfiguration;
import com.oo.tools.spring.boot.snowflake.service.SnowflakeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Snowflake Starter 测试类
 *
 * @author Yu.ou
 * @desc: 验证 Snowflake Starter 是否正常工作
 * @since: 1.0.0
 */
@SpringBootTest(classes = SnowflakeAutoConfiguration.class)
@TestPropertySource(properties = {
        "snowflake.enabled=true",
        "snowflake.prefix=test",
        "snowflake.instances.device.data-center-id=0",
        "snowflake.instances.device.epoch-timestamp=1672531200000",
        "spring.data.redis.host=localhost",
        "spring.data.redis.port=6379"
})
@DisplayName("Snowflake Starter 功能测试")
public class SnowflakeStarterTest {

    @Autowired(required = false)
    private SnowflakeService snowflakeService;

    @Test
    @DisplayName("验证 SnowflakeService Bean 是否创建成功")
    public void testSnowflakeServiceBeanExists() {
        assertThat(snowflakeService).isNotNull();
    }

    @Test
    @DisplayName("验证可以获取 Snowflake 实例")
    public void testGetSnowflake() {
        if (snowflakeService != null) {
            Snowflake snowflake = snowflakeService.getSnowflake("device");
            assertThat(snowflake).isNotNull();
        }
    }

    @Test
    @DisplayName("验证可以生成 ID")
    public void testGenerateId() {
        if (snowflakeService != null) {
            Snowflake snowflake = snowflakeService.getSnowflake("device");
            if (snowflake != null) {
                Long id = snowflake.nextId();
                assertThat(id).isNotNull();
                assertThat(id).isPositive();
                
                // 验证 ID 长度（18或19位）
                String idStr = String.valueOf(id);
                assertThat(idStr.length()).isBetween(18, 19);
                
                System.out.println("生成的 Snowflake ID: " + id);
                System.out.println("ID 字符串长度: " + idStr.length());
            }
        }
    }

    @Test
    @DisplayName("验证生成的 ID 唯一性")
    public void testIdUniqueness() {
        if (snowflakeService != null) {
            Snowflake snowflake = snowflakeService.getSnowflake("device");
            if (snowflake != null) {
                Long id1 = snowflake.nextId();
                Long id2 = snowflake.nextId();
                
                assertThat(id1).isNotEqualTo(id2);
                System.out.println("ID1: " + id1);
                System.out.println("ID2: " + id2);
            }
        }
    }
}

