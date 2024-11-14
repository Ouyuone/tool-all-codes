package cc.magicjson.caller.application.service;


import cc.magicjson.caller.domain.model.Endpoint;
import cc.magicjson.caller.domain.model.Service;
import cc.magicjson.caller.domain.discovery.ServiceDiscovery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DynamicServiceCallerTest {

    @Spy
    private ServiceDiscovery serviceDiscovery;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private DynamicServiceCaller serviceCaller;

    /**
     * 测试成功调用服务的场景
     */
    @Test
    public void callServiceShouldSucceed() {
        // 准备测试数据
        String serviceName = "testService";
        String endpointName = "testEndpoint";
        String request = "testRequest";
        Map<String, String> uriVars = Map.of("id", "123");

        Service service = new Service(serviceName, "http://test.com");
        Endpoint endpoint = new Endpoint(endpointName, "/api/test/{id}", "GET");

        // 配置模拟行为
        when(serviceDiscovery.getService(serviceName)).thenReturn(Optional.of(service));
//        when(serviceDiscovery.getEndpoints(Mockito.eq(v)).thenReturn(List.of(endpoint));
        when(serviceDiscovery.getEndpoint(serviceName,endpointName)).thenReturn(Optional.of(endpoint));

        ResponseEntity<String> expectedResponse = ResponseEntity.ok("testResponse");
        when(restTemplate.exchange(
                eq("http://test.com/api/test/{id}"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class),
                eq(uriVars)
        )).thenReturn(expectedResponse);

        // 执行测试
        String response = serviceCaller.callService(serviceName, endpointName, request, String.class, uriVars);

        // 验证结果
        assertEquals("testResponse", response, "响应应匹配预期值");
        verify(serviceDiscovery).getService(serviceName);
//        verify(serviceDiscovery).getEndpoints(serviceName);
        verify(serviceDiscovery).getEndpoint(serviceName,endpointName);
        verify(restTemplate).exchange(
                eq("http://test.com/api/test/{id}"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class),
                eq(uriVars)
        );
    }

    /**
     * 参数化测试：验证异常情况
     */
    @ParameterizedTest
    @MethodSource("exceptionTestCases")
    public void callServiceShouldThrowException(String serviceName, String endpointName,
                                                 Optional<Service> serviceOpt,
                                                 List<Endpoint> endpoints,
                                                 Class<? extends Exception> expectedException) {
        // 配置模拟行为
        when(serviceDiscovery.getService(serviceName)).thenReturn(serviceOpt);
        when(serviceDiscovery.getEndpoints(serviceName)).thenReturn(endpoints);
      

        // 执行测试并验证结果
        assertThrows(expectedException, () ->
                serviceCaller.callService(serviceName, endpointName, "request", String.class, Map.of()));
    }

    /**
     * 提供异常测试用例
     */
    private static Stream<Arguments> exceptionTestCases() {
        return Stream.of(
                Arguments.of("nonExistentService", "testEndpoint", Optional.empty(), List.of(), IllegalArgumentException.class),
                Arguments.of("testService", "nonExistentEndpoint", Optional.of(new Service("testService", "http://test.com")), List.of(), IllegalArgumentException.class)
        );
    }
}
