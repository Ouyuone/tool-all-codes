package cc.magicjson.caller.interfaces.rest;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 服务调用请求对象
 * 这个类定义了动态服务调用所需的参数
 *
 * @author <a href="mailto:it_magicjson@163.com">MagicJson</a>
 * @since 1.0.0
 */
@Getter
@Setter
public class ServiceCallRequest {
    private String serviceName;  // 要调用的服务名称
    private String endpointName; // 要调用的端点名称
    private Object payload;      // 请求负载
    private Map<String, String> uriVariables; // URL 路径变量
}
