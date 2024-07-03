package com.oo.tools.spring.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class App 
{
    public static void main( String[] args )
    {
        SpringApplication.run(App.class, args);

        System.out.println("swagger doc: http://127.0.0.1:1012/swagger-ui/index.html");
        System.out.println("knife doc: http://127.0.0.1:1012/doc.html");
    }
}
