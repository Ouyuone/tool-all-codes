package com.oo.tools.spring.boot;

import cn.hippo4j.config.springboot.starter.config.DynamicThreadPoolAutoConfiguration;
import cn.hippo4j.core.config.UtilAutoConfiguration;
import cn.hippo4j.core.enable.EnableDynamicThreadPool;
import cn.hippo4j.message.config.MessageConfiguration;
import cn.hippo4j.monitor.base.ThreadPoolMonitorConfig;
import cn.hippo4j.springboot.starter.adapter.web.WebAdapterConfiguration;
import cn.hippo4j.springboot.starter.monitor.micrometer.MicrometerMonitorAutoConfiguration;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Hello world!
 *
 */
@RestController
@RequestMapping
@SpringBootApplication
@EnableDynamicThreadPool
@PropertySource("classpath:application-hippo4j.yml")
@PropertySource("classpath:application-hippo4j-monitor.yml")
//@Import({MicrometerMonitorAutoConfiguration.class})
@ImportAutoConfiguration({MicrometerMonitorAutoConfiguration.class,UtilAutoConfiguration.class, ThreadPoolMonitorConfig.class})
public class AppApplication implements CommandLineRunner
{
    
    //获取nacos配置中心的配置
    @Value("${study.enable}")
    public Boolean studyEnable;
    
    @Resource
    private ThreadPoolExecutor messageConsumeDynamicExecutor;


    
    public static void main( String[] args )
    {
        SpringApplication.run(AppApplication.class, args);
    }
    
    @Override
    public void run(String... args) throws Exception {
        
        
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            messageConsumeDynamicExecutor.execute(() -> {
                String name = Thread.currentThread().getName();
                System.out.println("index: " + finalI + ", threadName: " + name + ", studyEnable: " + studyEnable);
            });
        }
    }
    
    @GetMapping("/threadRun")
    public Boolean threadRun(){
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            messageConsumeDynamicExecutor.execute(() -> {
                String name = Thread.currentThread().getName();
                System.out.println("index: " + finalI + ", threadName: " + name + ", studyEnable: " + studyEnable);
            });
        }
        return true;
    }
}

