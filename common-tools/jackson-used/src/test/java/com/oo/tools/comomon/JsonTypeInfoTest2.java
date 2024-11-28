package com.oo.tools.comomon;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oo.tools.comomon.domain.jsonTypeInfo.more.BaseCodeRepo;
import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2024/11/28 17:25:12
 */
@Slf4j
public class JsonTypeInfoTest2 extends TestCase {
    
    String JSON1;
    @Override
    protected void setUp() throws Exception {
        
        JSON1= """
                [
                    {
                        "id":1,
                        "type":"GITHUB",
                        "name":"Github代码库",
                        "path":"XuxuGood/jackjson",
                        "description":"JackJson多态解析"
                    },
                    {
                        "id":2,
                        "type":"GITLAB",
                        "name":"Gitlab代码库",
                        "path":"XuxuGood/jackjson",
                        "description":"JackJson多态解析"
                    }
                ]
                
                """;
    }
    
    public void testVersion1Json1() {
        String jsonString = JSON1;
        log.info("Json1字符串为: {}", jsonString);
        
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<BaseCodeRepo> baseCodeRepos = objectMapper.readValue(jsonString, new TypeReference<List<BaseCodeRepo>>() {
            });
            baseCodeRepos.forEach(item -> {
                try {
                    log.info(item.getType() + "对象信息为: {}", objectMapper.writeValueAsString(item));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
