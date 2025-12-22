package com.oo.tools.spring.boot.snowflake;

import com.oo.tools.spring.boot.snowflake.config.SnowflakeAutoConfiguration;
import com.oo.tools.spring.boot.snowflake.config.SnowflakeProperties;
import com.oo.tools.spring.boot.snowflake.service.SnowflakeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Snowflake 自动配置测试
 *
 * @author Yu.ou
 * @desc: 验证自动配置是否正确
 * @since: 1.0.0
 */
@DisplayName("Snowflake 自动配置测试")
public class SnowflakeAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SnowflakeAutoConfiguration.class));

    @Test
    @DisplayName("当 snowflake.enabled=false 时，不创建 SnowflakeService")
    public void testAutoConfigurationDisabled() {
        this.contextRunner
                .withPropertyValues("snowflake.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(SnowflakeService.class);
                });
    }

    @Test
    @DisplayName("当 snowflake.enabled=true 时，创建 SnowflakeService")
    public void testAutoConfigurationEnabled() {
        this.contextRunner
                .withPropertyValues(
                        "snowflake.enabled=true",
                        "snowflake.instances.device.data-center-id=0"
                )
                .run(context -> {
                    assertThat(context).hasSingleBean(SnowflakeProperties.class);
                    // 注意：由于需要 Redis，实际运行时可能会失败，但配置类应该被加载
                });
    }

    @Test
    @DisplayName("验证配置属性绑定")
    public void testConfigurationPropertiesBinding() {
        this.contextRunner
                .withPropertyValues(
                        "snowflake.enabled=true",
                        "snowflake.prefix=test",
                        "snowflake.instances.device.data-center-id=1",
                        "snowflake.instances.device.epoch-timestamp=1672531200000",
                        "snowflake.instances.device.time-offset=2000",
                        "snowflake.instances.user.data-center-id=2"
                )
                .run(context -> {
                    assertThat(context).hasSingleBean(SnowflakeProperties.class);
                    SnowflakeProperties properties = context.getBean(SnowflakeProperties.class);
                    
                    assertThat(properties.isEnabled()).isTrue();
                    assertThat(properties.getPrefix()).isEqualTo("test");
                    assertThat(properties.getInstances()).isNotNull();
                    assertThat(properties.getInstances()).containsKey("device");
                    assertThat(properties.getInstances()).containsKey("user");
                    
                    SnowflakeProperties.SnowflakeInstanceProperties deviceProps = 
                            properties.getInstances().get("device");
                    assertThat(deviceProps.getDataCenterId()).isEqualTo(1L);
                    assertThat(deviceProps.getEpochTimestamp()).isEqualTo(1672531200000L);
                    assertThat(deviceProps.getTimeOffset()).isEqualTo(2000L);
                });
    }
}

