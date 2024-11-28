package com.oo.tools.comomon;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.oo.tools.comomon.domain.jsonTypeName.BaseCodeRepo;
import com.oo.tools.comomon.domain.jsonTypeName.GithubRepo;
import com.oo.tools.comomon.domain.jsonTypeName.GitlabRepo;
import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jaxb.core.v2.ClassFactory;

import java.io.IOException;
import java.util.List;

/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2024/11/28 17:32:14
 */
@Slf4j
public class RemoveJsonSubTypeTest extends TestCase {
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
    
    public void testVersion2Json1() {
        ObjectMapper mapper = new ObjectMapper();
        // 注入多态解析类
//        ClassFactory.BASE_CODE_REPOS.forEach(mapper::registerSubtypes);
        //需要手动注入NamedType
        mapper.registerSubtypes(new NamedType(GithubRepo.class, "GITHUB"));
        mapper.registerSubtypes(new NamedType(GitlabRepo.class, "GITLAB"));
        String jsonString = JSON1;
        log.info("Json1字符串为: {}", jsonString);
        
        try {
            List<BaseCodeRepo> baseCodeRepos = mapper.readValue(jsonString, new TypeReference<List<BaseCodeRepo>>() {
            });
            baseCodeRepos.forEach(item -> {
                try {
                    log.info(item.getType() + "对象信息为: {}", mapper.writeValueAsString(item));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
