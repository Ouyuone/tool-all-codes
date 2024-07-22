package com.oo.tools.spring.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2024/07/22 17:32:13
 */
@BeansLoader(annotationClass = C.class)
@SpringBootApplication
public class App {
    
        public static void main(String[] args) {
        SpringApplication.run(App.class,args);
    }
}
