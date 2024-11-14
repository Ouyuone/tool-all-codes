package cc.magicjson.caller.infrastructure.http.factory;

import cc.magicjson.caller.infrastructure.http.config.HttpClientConfig;
import cc.magicjson.caller.infrastructure.http.request.OkHttpClientHttpRequest;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * OkHttp 的工厂实现
 *
 * @author <a href="mailto:it_magicjson@163.com">MagicJson</a>
 * @since 1.0.0
 */
@Component
@ConditionalOnProperty(name = "http.client.type", havingValue = "okhttp")
@RequiredArgsConstructor
public class OkHttpClientFactory implements HttpClientFactory {

    private final HttpClientConfig config;

    @Override
    public RestTemplate createRestTemplate() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(config.getConnectTimeout(), TimeUnit.MILLISECONDS)
            .readTimeout(config.getSocketTimeout(), TimeUnit.MILLISECONDS)
            .writeTimeout(config.getSocketTimeout(), TimeUnit.MILLISECONDS)
            .build();

        ClientHttpRequestFactory requestFactory = new OkHttpClientHttpRequestFactory(okHttpClient);
        return new RestTemplate(requestFactory);
    }

    /**
     * 自定义的 ClientHttpRequestFactory，使用 OkHttpClient
     */
    private record OkHttpClientHttpRequestFactory(OkHttpClient okHttpClient) implements ClientHttpRequestFactory {

        @NotNull
        @Override
        public ClientHttpRequest createRequest(@NotNull URI uri, @NotNull HttpMethod httpMethod){
            return new OkHttpClientHttpRequest(okHttpClient, uri, httpMethod);
        }
    }
}
