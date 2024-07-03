package com.oo.tools.spring.boot.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class Knife4jConfiguration {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("springshop-public")
                .pathsToMatch("/public/**")
                .build();
    }

    @Bean
    public GroupedOpenApi bodyApi() {
        return GroupedOpenApi.builder()
                .group("springshop-body")
                .pathsToMatch("/body/**")
                .packagesToScan("com.oo.tools.spring.boot.web")
//                .addOpenApiCustomizer(globalHeaderCustomiser())
//                .addOpenApiMethodFilter(method -> method.isAnnotationPresent(RestController.class))
                .build();
    }

    /**
     * 统一全局鉴权参数 如果这里不配置的话就需要加下面的Operation里面的security主动说明需要鉴权的iterm
     * @Operation(summary = "普通body请求",security = { @SecurityRequirement(name = HttpHeaders.AUTHORIZATION),@SecurityRequirement(name = "pp")})
     * or
     *  @SecurityRequirement(name = "pp")
     * @return
     */
    @Bean
    public GlobalOpenApiCustomizer orderGlobalOpenApiCustomizer() {
        return openApi -> {
            // 全局添加鉴权参数
            if(openApi.getPaths()!=null){
                openApi.getPaths().forEach((s, pathItem) -> {
                    // 为所有接口添加鉴权
                    pathItem.readOperations().forEach(operation -> {
                        operation.addSecurityItem(new SecurityRequirement().addList("pp").addList(HttpHeaders.AUTHORIZATION));
                    });
                });
            }

        };
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("XXX用户系统API")
                        .version("1.0")
                        .description( "Knife4j集成springdoc-openapi示例")
                        .termsOfService("http://doc.xiaominfo.com")
                        .license(new License().name("Apache 2.0")
                                .url("http://doc.xiaominfo.com"))
                )
                //下面是添加鉴权参数iterm
                .addSecurityItem(new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION))
                .components(new Components()
                        .addSecuritySchemes(HttpHeaders.AUTHORIZATION,new SecurityScheme()
                                .name(HttpHeaders.AUTHORIZATION)
                                .type(SecurityScheme.Type.HTTP)
                                .in(SecurityScheme.In.HEADER)
                                .scheme("bearer"))
                        .addSecuritySchemes("pp", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("pps"))
                );

    }
}