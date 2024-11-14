package cc.magicjson.caller.infrastructure.http;

import cc.magicjson.caller.infrastructure.http.config.HttpClientConfig;
import cc.magicjson.caller.infrastructure.http.factory.ApacheHttpClientFactory;
import cc.magicjson.caller.infrastructure.http.factory.HttpClientFactory;
import cc.magicjson.caller.infrastructure.http.factory.OkHttpClientFactory;
import org.springframework.stereotype.Component;

/**
 * HTTP 客户端工厂提供器
 *
 * @author <a href="mailto:it_magicjson@163.com">MagicJson</a>
 * @since 1.0.0
 */
@Component
public class HttpClientFactoryProvider {

    private final HttpClientConfig config;

    public HttpClientFactoryProvider(HttpClientConfig config) {
        this.config = config;
    }

    public HttpClientFactory getHttpClientFactory() {
        return switch (config.getType()) {
            case APACHE -> new ApacheHttpClientFactory(config);
            case OKHTTP -> new OkHttpClientFactory(config);
            // 在这里添加其他 HTTP 客户端类型的 case
        };
    }
}
