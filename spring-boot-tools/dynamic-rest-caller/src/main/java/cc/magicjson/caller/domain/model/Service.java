package cc.magicjson.caller.domain.model;

/**
 * 表示系统中的一个服务。
 * 这个记录包含了关于服务的基本信息
 *
 * @author <a href="mailto:it_magicjson@163.com">MagicJson</a>
 * @since 1.0.0
 *
 * @param name 服务的名称
 * @param url 服务的基础 URL
 */
public record Service(String name, String url) {}
