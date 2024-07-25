package com.oo.tools.spring.boot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2024/07/25 10:43:17
 */
public class AppTest {
    
    public static void main(String[] args) {
        
        //动态的给对象加字段
        Object target = BeanAddPropertiesUtil.getTarget(new Object(), Map.of("name", "ouyu"));
        System.out.println(target);
        
        String jsonString = JSONObject.toJSONString(target);
        System.out.println(jsonString);
        ObjectMapper objectMapper = new ObjectMapper();
        String s = null;
        try {
            s = objectMapper.writeValueAsString(target);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println(s);
    }
    
    public static class A{
    
    
    }
}
