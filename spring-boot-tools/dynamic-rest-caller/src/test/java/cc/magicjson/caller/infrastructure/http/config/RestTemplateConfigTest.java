package cc.magicjson.caller.infrastructure.http.config;


import cc.magicjson.caller.infrastructure.http.HttpClientFactoryProvider;
import cc.magicjson.caller.infrastructure.http.factory.HttpClientFactory;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RestTemplateConfigTest {

    @Mock
    private HttpClientFactoryProvider factoryProvider;

    @Mock
    private HttpClientFactory clientFactory;

    @InjectMocks
    private RestTemplateConfig restTemplateConfig;

    /**
     * 测试RestTemplate的创建过程
     * 验证是否正确使用HttpClientFactory创建RestTemplate
     */
    @Test
    public void createRestTemplateShouldUseFactory() {
        // 准备测试数据
        RestTemplate expectedTemplate = new RestTemplate();

        // 设置模拟行为
        when(factoryProvider.getHttpClientFactory()).thenReturn(clientFactory);
        when(clientFactory.createRestTemplate()).thenReturn(expectedTemplate);

        // 执行测试
        RestTemplate actualTemplate = restTemplateConfig.restTemplate();

        // 验证结果
        assertSame(expectedTemplate, actualTemplate, "应返回由HttpClientFactory创建的RestTemplate");
        verify(factoryProvider).getHttpClientFactory();
        verify(clientFactory).createRestTemplate();
    }
}
