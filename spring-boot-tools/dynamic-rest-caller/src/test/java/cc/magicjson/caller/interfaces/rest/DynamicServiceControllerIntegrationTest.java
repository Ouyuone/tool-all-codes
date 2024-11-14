package cc.magicjson.caller.interfaces.rest;

import cc.magicjson.caller.application.service.DynamicServiceCaller;

import cc.magicjson.caller.domain.model.Endpoint;
import cc.magicjson.caller.domain.model.Service;
import cc.magicjson.caller.domain.discovery.ServiceDiscovery;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DynamicServiceController.class)
public class DynamicServiceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DynamicServiceCaller serviceCaller;

    @MockBean
    private ServiceDiscovery serviceDiscovery;

    /**
     * 测试有效请求的服务调用
     */
    @Test
    public void callServiceShouldSucceed() throws Exception {
        // 准备测试数据
        String serviceName = "testService";
        String endpointName = "testEndpoint";
        Map<String, String> uriVars = Map.of("id", "123");

        ServiceCallRequest request = new ServiceCallRequest();
        request.setServiceName(serviceName);
        request.setEndpointName(endpointName);
        request.setPayload("testPayload");
        request.setUriVariables(uriVars);

        String expectedResponse = "Test response";

        // 配置模拟行为
        when(serviceDiscovery.getService(serviceName)).thenReturn(Optional.of(new Service(serviceName, "http://test.com")));
        when(serviceDiscovery.getEndpoints(serviceName)).thenReturn(List.of(new Endpoint(endpointName, "/api/test", "GET")));
        when(serviceCaller.callService(eq(serviceName), eq(endpointName), any(), eq(Object.class), eq(uriVars)))
            .thenReturn(expectedResponse);

        // 执行测试并验证结果
        mockMvc.perform(post("/call")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().string(expectedResponse));
    }

    /**
     * 测试服务不存在时的错误处理
     */
    @Test
    public void callServiceShouldFailWhenServiceNotFound() throws Exception {
        // 准备测试数据
        ServiceCallRequest request = new ServiceCallRequest();
        request.setServiceName("nonExistentService");
        request.setEndpointName("testEndpoint");

        // 配置模拟行为
        when(serviceDiscovery.getService("nonExistentService")).thenReturn(Optional.empty());

        // 执行测试并验证结果
        mockMvc.perform(post("/call")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Service not found: nonExistentService"));
    }
}
