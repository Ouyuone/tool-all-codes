package cc.magicjson.caller.infrastructure.http.config;

import cc.magicjson.caller.infrastructure.http.type.HttpClientType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * http客户端配置类
 *
 * @author <a href="mailto:it_magicjson@163.com">MagicJson</a>
 * @since 1.0.0
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "http.client")
public class HttpClientConfig {
    private HttpClientType type = HttpClientType.APACHE;
    private int maxTotal = 100;
    private int defaultMaxPerRoute = 20;
    private int connectTimeout = 5000;
    private int socketTimeout = 65000;
}
