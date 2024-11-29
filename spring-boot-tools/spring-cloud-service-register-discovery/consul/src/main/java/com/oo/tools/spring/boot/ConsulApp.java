package com.oo.tools.spring.boot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Consul注册中心的学习使用
 * Consul服务配置中心的使用
 *
 */
@RefreshScope //加到启动类上会导致CommandLineRunner接口执行两次
@RestController
@EnableDiscoveryClient
@SpringBootApplication
public class ConsulApp implements CommandLineRunner
{
   
    @Value("${name:ss}")
    private String name;
    
    public static void main( String[] args )
    {
        SpringApplication.run(ConsulApp.class, args);
    }
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println(name);
    }
    
    @GetMapping
    public String hello(){
        return "hello world" +name;
    }
}
