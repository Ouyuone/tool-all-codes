package cc.magicjson.caller.infrastructure.http.factory;

import org.springframework.web.client.RestTemplate;

/**
 * HTTP 客户端工厂接口
 * 用于创建 RestTemplate 实例
 *
 * @author <a href="mailto:it_magicjson@163.com">MagicJson</a>
 * @since 1.0.0
 */
public interface HttpClientFactory {
    /**
     * 创建 RestTemplate 实例
     *
     * @return 配置好的 RestTemplate
     */
    RestTemplate createRestTemplate();
}
