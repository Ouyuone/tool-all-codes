package cc.magicjson.caller.infrastructure.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 配置类
 *
 * @author <a href="mailto:it_magicjson@163.com">MagicJson</a>
 * @since 1.0.0
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Spring Boot 3 API 示例")
                        .version("1.0")
                        .description("这是一个使用 Spring Boot 3 和 SpringDoc 的 API 文档示例")
                        .termsOfService("http://swagger.io/terms/")
                        .contact(new Contact().name("API 支持团队").email("support@example.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("springshop-public")
                .pathsToMatch("/**")
                .build();
    }
}
