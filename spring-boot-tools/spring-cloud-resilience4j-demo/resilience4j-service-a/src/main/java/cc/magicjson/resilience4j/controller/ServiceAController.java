package cc.magicjson.resilience4j.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

// 定义Service A的REST控制器
@RestController
public class ServiceAController {

    // 注入RestTemplate用于发起HTTP请求
    private final RestTemplate restTemplate;

    // 构造函数，通过依赖注入获取RestTemplate实例
    public ServiceAController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // 定义一个简单的GET端点
    @GetMapping("/hello")
    public String hello() {
        return "Hello from Service A";
    }

    // 定义一个调用Service B的端点，并使用Circuit Breaker进行保护
    @GetMapping("/call-b")
    @CircuitBreaker(name = "serviceB", fallbackMethod = "fallbackForServiceB")
    public String callServiceB() {
        System.out.println("Calling Service B");
        return restTemplate.getForObject("http://localhost:8082/hello", String.class);
    }

    // 定义一个端点直接触发fallback条件
    @GetMapping("/call-b-fallback")
    @CircuitBreaker(name = "serviceB", fallbackMethod = "fallbackForServiceB")
    public String callServiceBFallback() {
        System.out.println("Calling Service B (Fallback)");
        throw new RuntimeException("Simulated failure");
    }

    // 定义Circuit Breaker的回退方法，当调用Service B失败时执行
    public String fallbackForServiceB(Exception e) {
        System.out.println("Fallback method called: " + e.getMessage());
        return "Fallback: Service B is not available";
    }


}
