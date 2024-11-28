package cc.magicjson.resilience4j;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

// 使用随机端口启动完整的Spring应用程序上下文
@SpringBootTest(classes = GatewayApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GatewayServiceControllerTest {

    // 注入TestRestTemplate用于发送HTTP请求
    @Autowired
    private TestRestTemplate testRestTemplate;

    // 模拟RestTemplate，用于控制Service B的响应
    @MockBean
    private RestTemplate restTemplate;

    // 测试成功调用Service B的情况
    @Test
    public void testCallServiceB_Success() {
        // 模拟Service B返回成功响应
        when(restTemplate.getForObject("http://localhost:8082/hello", String.class))
            .thenReturn("Hello from Service B");

        // 调用Service A的/call-b端点
        ResponseEntity<String> response = testRestTemplate.getForEntity("/service-a/call-b", String.class);

        // 验证响应状态和内容
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Hello from Service B", response.getBody());
    }

    // 测试调用Service B失败的情况
    @Test
    public void testCallServiceB_Failure() {
        // 模拟Service B抛出异常
        when(restTemplate.getForObject("http://localhost:8082/hello", String.class))
            .thenThrow(new RuntimeException("Service B is down"));

        // 调用Service A的/call-b端点
        ResponseEntity<String> response = testRestTemplate.getForEntity("/service-a/call-b", String.class);

        // 验证响应状态和回退内容
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Fallback: Service B is not available", response.getBody());
    }

    @Test
    public void testCallServiceB_DirectFallback() {
        ResponseEntity<String> response = testRestTemplate.getForEntity("/service-a/call-b-fallback", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Fallback: Service B is not available", response.getBody());
    }

    // 测试断路器功能
    @Test
    public void testCallServiceB_CircuitBreaker() throws InterruptedException {
        // 模拟Service B持续失败
        when(restTemplate.getForObject("http://localhost:8082/hello", String.class))
            .thenThrow(new RuntimeException("Service B is down"));

        // 多次调用服务以打开断路器
        for (int i = 0; i < 10; i++) {
            testRestTemplate.getForEntity("/service-a/call-b", String.class);
        }

        // 断路器应该已经打开，直接调用回退方法
        ResponseEntity<String> response = testRestTemplate.getForEntity("/service-a/call-b", String.class);

        // 验证响应状态和回退内容
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Fallback: Service B is not available", response.getBody());

        // 等待断路器关闭（根据配置调整等待时间）
        Thread.sleep(11000);

        // 断路器应该已关闭，重新尝试调用Service B
        when(restTemplate.getForObject("http://localhost:8082/hello", String.class))
            .thenReturn("Hello from Service B");

        response = testRestTemplate.getForEntity("/service-a/call-b", String.class);

        // 验证响应状态和内容
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Hello from Service B", response.getBody());
    }
}
