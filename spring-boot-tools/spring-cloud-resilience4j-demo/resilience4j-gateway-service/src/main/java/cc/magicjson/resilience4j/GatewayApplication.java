package cc.magicjson.resilience4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

// 定义Spring Boot应用程序的主类
@SpringBootApplication
public class GatewayApplication {

    // 应用程序入口点
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    // 配置自定义路由 第二种配置gateway的方式 使用RouteLocatorBuilder
//    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            // 配置路由到service-a的路径
            .route("service-a", r -> r.path("/service-a/**")
                .filters(f -> f
                    // 使用CircuitBreaker进行熔断保护
                    .circuitBreaker(config -> config
                        .setName("service-a")
                        .setFallbackUri("forward:/fallback"))
                    // 去除路径前缀
                    .rewritePath("/service-a/(?<segment>.*)", "/${segment}"))
                .uri("http://localhost:8081"))
            // 配置路由到service-b的路径
            .route("service-b", r -> r.path("/service-b/**")
                .filters(f -> f
                    // 使用CircuitBreaker进行熔断保护
                    .circuitBreaker(config -> config
                        .setName("service-b")
                        .setFallbackUri("forward:/fallback"))
                    // 去除路径前缀
                    .rewritePath("/service-b/(?<segment>.*)", "/${segment}"))
                .uri("http://localhost:8082"))
            .build();
    }
}
