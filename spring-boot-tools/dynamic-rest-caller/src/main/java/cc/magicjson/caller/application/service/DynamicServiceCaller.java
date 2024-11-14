package cc.magicjson.caller.application.service;


import cc.magicjson.caller.domain.discovery.ServiceDiscovery;
import cc.magicjson.caller.domain.model.Endpoint;
import cc.magicjson.caller.domain.model.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.util.Map;

import static java.util.Objects.nonNull;

/**
 * 动态调用其他服务的服务类。
 * 这个类使用 ServiceDiscovery 来定位服务及其端点，
 * 然后使用 RestTemplate 来进行实际的 HTTP 调用。
 *
 * @author <a href="mailto:it_magicjson@163.com">MagicJson</a>
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DynamicServiceCaller {
    private final ServiceDiscovery serviceDiscovery;
    private final RestTemplate restTemplate;

    /**
     * 动态调用服务端点。
     *
     * @param serviceName  要调用的服务名称
     * @param endpointName 要调用的端点名称
     * @param request      请求体
     * @param responseType 预期的响应类型
     * @param uriVariables 要替换在 URL 路径中的变量
     * @param <T>          响应的类型
     * @return 来自服务调用的响应
     * @throws IllegalArgumentException 如果找不到服务或端点
     */
    public <T> T callService(String serviceName, String endpointName, Object request, Class<T> responseType, Map<String, ?> uriVariables) {
        Service service = serviceDiscovery.getService(serviceName)
            .orElseThrow(() -> new IllegalArgumentException("Service not found: " + serviceName));

        Endpoint endpoint = serviceDiscovery.getEndpoint(serviceName, endpointName)
            .orElseThrow(() -> new IllegalArgumentException("Endpoint not found: " + endpointName));

        String baseUrl = service.url();
        String path = endpoint.path();

        // 构建 URI
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl).path(path);

        // 添加查询参数（如果有的话）
        // 处理 URI 变量和查询参数
        if (nonNull(uriVariables)) {
            for (Map.Entry<String, ?> entry : uriVariables.entrySet()) {
                String key = entry.getKey();
                String value = (String) entry.getValue();
                if (path.contains("{" + key + "}")) {
                    // 这是一个路径变量
                    path = path.replace("{" + key + "}", UriUtils.encodePathSegment(value, "UTF-8"));
                } else {
                    // 这是一个查询参数
                    builder.queryParam(key, value);
                }
            }
        }

        // 构建最终的 URI
        String uri = builder.build(false).toUriString();


        // 打印拼接后的 URI和PATH
        log.info("Calling service with URI: {}----> PATH: {}", uri,path);

        return restTemplate.exchange(
            uri,
            HttpMethod.valueOf(endpoint.method()),
            new HttpEntity<>(request),
            responseType,
            uriVariables
        ).getBody();
    }
}
