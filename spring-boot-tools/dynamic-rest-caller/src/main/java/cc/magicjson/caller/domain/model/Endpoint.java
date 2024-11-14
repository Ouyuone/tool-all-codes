package cc.magicjson.caller.domain.model;

/**
 * 表示服务的一个端点。
 * 这个记录包含了关于端点的基本信息
 *
 * @author <a href="mailto:it_magicjson@163.com">MagicJson</a>
 * @since 1.0.0
 *
 * @param name 端点的名称
 * @param path 端点的路径
 * @param method 端点的 HTTP 方法
 */
public record Endpoint(String name, String path, String method) {}
