package cc.magicjson.caller.domain.discovery;


import cc.magicjson.caller.domain.model.Endpoint;
import cc.magicjson.caller.domain.model.Service;

import java.util.List;
import java.util.Optional;

/**
 * 服务发现操作的接口。
 * 这个接口定义了发现服务及其端点的契约。
 *
 * @author <a href="mailto:it_magicjson@163.com">MagicJson</a>
 * @since 1.0.0
 *
 */
public interface ServiceDiscovery {
    /**
     * 通过名称检索服务。
     *
     * @param serviceName 要检索的服务名称
     * @return 包含 Service 的 Optional，如果找到则有值，否则为空
     */
    Optional<Service> getService(String serviceName);

    /**
     * 检索给定服务的所有端点。
     *
     * @param serviceName 要检索端点的服务名称
     * @return 给定服务的 Endpoint 列表
     */
    List<Endpoint> getEndpoints(String serviceName);



    /**
     * 获取指定服务的指定端点
     *
     * @param serviceName 服务名称
     * @param endpointName 端点名称
     * @return 包含 Endpoint 的 Optional，如果找到则有值，否则为空
     */
    Optional<Endpoint> getEndpoint(String serviceName, String endpointName);
}
