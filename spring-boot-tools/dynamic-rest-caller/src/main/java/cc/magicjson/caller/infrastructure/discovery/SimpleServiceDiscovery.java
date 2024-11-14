package cc.magicjson.caller.infrastructure.discovery;

import cc.magicjson.caller.domain.discovery.ServiceDiscovery;
import cc.magicjson.caller.domain.model.Endpoint;
import cc.magicjson.caller.domain.model.Service;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * SimpleDiscovery 类实现了 ServiceDiscovery 接口，提供了一个简单的服务发现机制。
 * 这个类模拟了一个服务注册表，存储了预定义的服务和端点信息
 *
 * @author <a href="mailto:it_magicjson@163.com">MagicJson</a>
 * @since 1.0.0
 */
@Component
public class SimpleServiceDiscovery implements ServiceDiscovery {

    // 存储服务信息的 Map，键为服务名，值为 Service 对象
    private final Map<String, Service> services;
    // 存储端点信息的 Map，键为服务名，值为该服务的端点列表
    private final Map<String, List<Endpoint>> endpoints;

    /**
     * 构造函数，初始化服务和端点信息
     */
    public SimpleServiceDiscovery() {
        services = new HashMap<>();
        endpoints = new HashMap<>();

        // 初始化预定义的服务和端点
        initializeServices();
    }

    /**
     * 初始化预定义的服务和端点信息
     * 这个方法模拟了服务注册的过程
     */
    private void initializeServices() {
        // 初始化 userService
        Service userService = new Service("userService", "http://localhost:8080");
        services.put("userService", userService);
        endpoints.put("userService", List.of(
            new Endpoint("getUserInfo", "/users/{id}", "GET"),
            new Endpoint("createUser", "/users", "POST")
        ));

        // 初始化 orderService
        Service orderService = new Service("orderService", "http://localhost:8080");
        services.put("orderService", orderService);
        endpoints.put("orderService", List.of(
            new Endpoint("createOrder", "/orders", "POST"),
            new Endpoint("getOrderDetails", "/orders/{id}", "GET")
        ));

        // 初始化 productService
        Service productService = new Service("productService", "http://localhost:8080");
        services.put("productService", productService);
        endpoints.put("productService", List.of(
            new Endpoint("searchProducts", "/products/search", "GET"),
            new Endpoint("getProductDetails", "/products/{id}", "GET")
        ));
    }

    /**
     * 根据服务名获取服务信息
     * @param serviceName 服务名
     * @return 包含 Service 对象的 Optional，如果服务不存在则返回空 Optional
     */
    @Override
    public Optional<Service> getService(String serviceName) {
        return Optional.ofNullable(services.get(serviceName));
    }

    /**
     * 获取指定服务的所有端点
     * @param serviceName 服务名
     * @return 端点列表，如果服务不存在则返回空列表
     */
    @Override
    public List<Endpoint> getEndpoints(String serviceName) {
        return endpoints.getOrDefault(serviceName, List.of());
    }

    /**
     * 获取指定服务的特定端点
     * @param serviceName 服务名
     * @param endpointName 端点名
     * @return 包含 Endpoint 对象的 Optional，如果端点不存在则返回空 Optional
     */
    @Override
    public Optional<Endpoint> getEndpoint(String serviceName, String endpointName) {
        return getEndpoints(serviceName).stream()
            .filter(endpoint -> endpoint.name().equals(endpointName))
            .findFirst();
    }
}
