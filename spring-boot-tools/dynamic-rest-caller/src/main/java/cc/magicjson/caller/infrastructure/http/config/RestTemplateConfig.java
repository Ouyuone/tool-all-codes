package cc.magicjson.caller.infrastructure.http.config;


import cc.magicjson.caller.infrastructure.http.HttpClientFactoryProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate 配置类
 *
 * @author <a href="mailto:it_magicjson@163.com">MagicJson</a>
 * @since 1.0.0
 */
@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {

    private final HttpClientFactoryProvider httpClientFactoryProvider;

    @Bean
    public RestTemplate restTemplate() {
        return httpClientFactoryProvider.getHttpClientFactory().createRestTemplate();
    }
}
