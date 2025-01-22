package com.oo.tools.spring.boot;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2025/01/08 10:15:37
 */
@Slf4j
public class LifeCycleBean implements
        ApplicationContextAware,
        BeanFactoryAware,
        InitializingBean,
        SmartLifecycle,
        BeanNameAware,
        ApplicationListener<ContextRefreshedEvent>,
        CommandLineRunner,
        SmartInitializingSingleton {
    
    private AutoWriedFiled autoWriedFiled;
    
    @Autowired
    public void setAutoWriedFiled(AutoWriedFiled autoWriedFiled) {
        log.info("启动顺序：{}","设置属性 setName");
        this.autoWriedFiled = autoWriedFiled;
    }
    
    public LifeCycleBean() {
        log.info("启动顺序：{}","无参构造");
    }
    
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        log.info("启动顺序：{}","BeanFactoryAware");
    }
    
    @Override
    public void setBeanName(String name) {
        log.info("启动顺序：{}","BeanNameAware");
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("启动顺序：{}","InitializingBean afterPropertiesSet");
    }
    
    @Override
    public void afterSingletonsInstantiated() {
        log.info("启动顺序：{}","SmartInitializingSingleton afterSingletonsInstantiated");
    }
    
    @Override
    public void run(String... args) throws Exception {
        log.info("启动顺序：{}","CommandLineRunner");
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        log.info("启动顺序：{}","ApplicationContextAware");
    }
    
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("启动顺序：{}","ApplicationListener<ContextRefreshedEvent>");
    }
    
    @Override
    public void start() {
        log.info("启动顺序：{}","SmartLifecycle start");
    }
    
    @Override
    public void stop() {
        log.info("启动顺序：{}","SmartLifecycle stop");
    }
    
    @Override
    public boolean isRunning() {
        return false;
    }
    @PostConstruct
    public void postConstruct() {
        log.info("启动顺序：{}","post-construct");
    }
    
    public void initMethod() {
        log.info("启动顺序：{}","init-method");
    }
    
    /*
    *
2025-01-08T10:35:01.662+08:00  INFO 94036 --- [spring-boot-life-cycle] [           main] c.o.t.spring.boot.LifeCycleBeanConfig    : 启动顺序: @Bean 注解方法执行 LifeCycleBeanOrder2
2025-01-08T10:35:01.662+08:00 ERROR 94036 --- [spring-boot-life-cycle] [           main] c.o.t.spring.boot.LifeCycleBeanOrder2    : 启动顺序:BeanFactoryPostProcessor postProcessBeanFactory
2025-01-08T10:35:01.671+08:00  INFO 94036 --- [spring-boot-life-cycle] [           main] c.o.t.spring.boot.LifeCycleBeanConfig    : 启动顺序: @Bean 注解方法执行 lifeCycleBean
2025-01-08T10:35:01.673+08:00  INFO 94036 --- [spring-boot-life-cycle] [           main] com.oo.tools.spring.boot.LifeCycleBean   : 启动顺序：无参构造
2025-01-08T10:35:01.676+08:00  INFO 94036 --- [spring-boot-life-cycle] [           main] com.oo.tools.spring.boot.LifeCycleBean   : 启动顺序：设置属性 setName
2025-01-08T10:35:01.676+08:00  INFO 94036 --- [spring-boot-life-cycle] [           main] com.oo.tools.spring.boot.LifeCycleBean   : 启动顺序：BeanNameAware
2025-01-08T10:35:01.676+08:00  INFO 94036 --- [spring-boot-life-cycle] [           main] com.oo.tools.spring.boot.LifeCycleBean   : 启动顺序：BeanFactoryAware
2025-01-08T10:35:01.676+08:00  INFO 94036 --- [spring-boot-life-cycle] [           main] com.oo.tools.spring.boot.LifeCycleBean   : 启动顺序：ApplicationContextAware
2025-01-08T10:35:01.676+08:00 ERROR 94036 --- [spring-boot-life-cycle] [           main] c.o.t.spring.boot.LifeCycleBeanOrder2    : 启动顺序:BeanPostProcessor postProcessBeforeInitialization beanName:lifeCycleBean
2025-01-08T10:35:01.676+08:00  INFO 94036 --- [spring-boot-life-cycle] [           main] com.oo.tools.spring.boot.LifeCycleBean   : 启动顺序：post-construct
2025-01-08T10:35:01.676+08:00  INFO 94036 --- [spring-boot-life-cycle] [           main] com.oo.tools.spring.boot.LifeCycleBean   : 启动顺序：InitializingBean afterPropertiesSet
2025-01-08T10:35:01.677+08:00  INFO 94036 --- [spring-boot-life-cycle] [           main] com.oo.tools.spring.boot.LifeCycleBean   : 启动顺序：init-method
2025-01-08T10:35:01.677+08:00 ERROR 94036 --- [spring-boot-life-cycle] [           main] c.o.t.spring.boot.LifeCycleBeanOrder2    : 启动顺序:BeanPostProcessor postProcessAfterInitialization beanName:lifeCycleBean
2025-01-08T10:35:01.704+08:00  INFO 94036 --- [spring-boot-life-cycle] [           main] com.oo.tools.spring.boot.LifeCycleBean   : 启动顺序：SmartInitializingSingleton afterSingletonsInstantiated
2025-01-08T10:35:04.048+08:00  INFO 94036 --- [spring-boot-life-cycle] [           main] com.oo.tools.spring.boot.LifeCycleBean   : 启动顺序：SmartLifecycle start
2025-01-08T10:35:04.055+08:00  INFO 94036 --- [spring-boot-life-cycle] [           main] com.oo.tools.spring.boot.LifeCycleBean   : 启动顺序：ApplicationListener<ContextRefreshedEvent>
2025-01-08T10:35:04.059+08:00  INFO 94036 --- [spring-boot-life-cycle] [           main] c.o.t.s.b.SpringBootLifeCycleApplication : Started SpringBootLifeCycleApplication in 2.679 seconds (process running for 2.959)
2025-01-08T10:35:04.063+08:00  INFO 94036 --- [spring-boot-life-cycle] [           main] com.oo.tools.spring.boot.LifeCycleBean   : 启动顺序：CommandLineRunner
    *
    * */
    
}
