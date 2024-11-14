package cc.magicjson.caller.infrastructure.http;

import cc.magicjson.caller.infrastructure.http.config.HttpClientConfig;
import cc.magicjson.caller.infrastructure.http.factory.ApacheHttpClientFactory;
import cc.magicjson.caller.infrastructure.http.factory.HttpClientFactory;
import cc.magicjson.caller.infrastructure.http.factory.OkHttpClientFactory;
import cc.magicjson.caller.infrastructure.http.type.HttpClientType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HttpClientFactoryProviderTest {

    @Mock
    private HttpClientConfig config;

    @InjectMocks
    private HttpClientFactoryProvider provider;

    /**
     * 测试获取工厂方法
     * 验证不同HTTP客户端类型是否返回正确的工厂实例
     * @param type HTTP客户端类型
     */
    @ParameterizedTest
    @EnumSource(HttpClientType.class)
    public void getFactoryShouldMatchType(HttpClientType type) {
        // 设置模拟配置返回指定类型
        when(config.getType()).thenReturn(type);

        // 获取工厂实例
        HttpClientFactory factory = provider.getHttpClientFactory();

        // 根据类型确定期望的工厂类
        Class<?> expectedClass = type == HttpClientType.APACHE ?
            ApacheHttpClientFactory.class : OkHttpClientFactory.class;

        // 验证返回的工厂实例类型是否符合预期
        assertTrue(expectedClass.isInstance(factory),
                String.format("工厂实例应为 %s 类型", expectedClass.getSimpleName()));
    }
}
