package com.oo.tools.spring.boot;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Unit test for simple App.
 */

@SpringBootTest(classes = App.class)
public class AppTest
{
    
    @Autowired
    private A a;
    
    
    
    @Test
    public void test_qidong(){
        System.out.println(a);
    }

}
