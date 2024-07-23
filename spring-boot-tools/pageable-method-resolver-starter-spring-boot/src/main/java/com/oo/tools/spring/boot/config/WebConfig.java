package com.oo.tools.spring.boot.config;

import com.oo.tools.spring.boot.method.argument.resolver.PageableServletModelAttributeMethodProcessor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

import java.util.List;

/**
 * 第二种方式
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2024/07/23 11:12:20
 */
//@Configuration
public class WebConfig implements WebMvcConfigurer, ApplicationContextAware {
    private ApplicationContext context;
    
    
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new PageableServletModelAttributeMethodProcessor(
                new ServletModelAttributeMethodProcessor(true),
                context.getBean("pageableResolver", PageableHandlerMethodArgumentResolver.class)));
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
