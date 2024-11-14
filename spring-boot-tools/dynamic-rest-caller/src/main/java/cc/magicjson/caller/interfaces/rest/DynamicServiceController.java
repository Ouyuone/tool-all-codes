package cc.magicjson.caller.interfaces.rest;

import cc.magicjson.caller.application.service.DynamicServiceCaller;

import cc.magicjson.caller.domain.discovery.ServiceDiscovery;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务调用控制器
 *
 * @author <a href="mailto:it_magicjson@163.com">MagicJson</a>
 * @since 1.0.0
 */
@RequiredArgsConstructor
@RestController
public class DynamicServiceController {

    private final DynamicServiceCaller serviceCaller;
    private final ServiceDiscovery serviceDiscovery;

    /**
     * 服务调用
     *
     * @param request 服务调用请求 包含服务名称、端点名称、请求参数、URI变量
     * @return 对应服务调用的响应
     */
    @PostMapping("/call")
    public ResponseEntity<?> callService(@RequestBody ServiceCallRequest request) {
        // 检查服务是否存在
        if (serviceDiscovery.getService(request.getServiceName()).isEmpty()) {
            return ResponseEntity.badRequest().body("Service not found: " + request.getServiceName());
        }
        if (serviceDiscovery.getEndpoint(request.getServiceName(), request.getEndpointName()).isEmpty()) {
            return ResponseEntity.badRequest().body("Endpoint not found: " + request.getServiceName());
        }

        Object response = serviceCaller.callService(
            request.getServiceName(),
            request.getEndpointName(),
            request.getPayload(),
            Object.class,
            request.getUriVariables()
        );
        return ResponseEntity.ok(response);
    }
}
