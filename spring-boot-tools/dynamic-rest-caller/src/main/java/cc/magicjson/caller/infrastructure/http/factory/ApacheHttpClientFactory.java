package cc.magicjson.caller.infrastructure.http.factory;

import cc.magicjson.caller.infrastructure.http.config.HttpClientConfig;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Apache HttpClient 5 的工厂实现
 *
 * @author <a href="mailto:it_magicjson@163.com">MagicJson</a>
 * @since 1.0.0
 */
@Component
@ConditionalOnProperty(name = "http.client.type", havingValue = "apache", matchIfMissing = true)
@RequiredArgsConstructor
public class ApacheHttpClientFactory implements HttpClientFactory {

    private final HttpClientConfig config;

    @Override
    public RestTemplate createRestTemplate() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(config.getMaxTotal());
        connectionManager.setDefaultMaxPerRoute(config.getDefaultMaxPerRoute());

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        requestFactory.setConnectTimeout(Timeout.ofMilliseconds(config.getConnectTimeout()).toDuration());
        requestFactory.setConnectionRequestTimeout(Timeout.ofMilliseconds(config.getSocketTimeout()).toDuration());

        return new RestTemplate(requestFactory);
    }
}
