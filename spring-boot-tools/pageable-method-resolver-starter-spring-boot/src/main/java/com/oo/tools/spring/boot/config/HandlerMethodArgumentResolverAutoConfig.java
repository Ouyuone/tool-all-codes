package com.oo.tools.spring.boot.config;

import com.oo.tools.spring.boot.method.argument.resolver.CustomRequestResponseBodyMethodProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * 第一种方式
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2024/02/22 13:39:44
 */
//@Configuration
@RequiredArgsConstructor
public class HandlerMethodArgumentResolverAutoConfig implements InitializingBean {

    private final RequestMappingHandlerAdapter requestMappingHandlerAdapter;


    @Override
    public void afterPropertiesSet() {
        List<HandlerMethodArgumentResolver> argumentResolvers = requestMappingHandlerAdapter.getArgumentResolvers();
        List<HandlerMethodArgumentResolver> customArgumentResolvers = new ArrayList<>();

        for (HandlerMethodArgumentResolver argumentResolver : argumentResolvers) {
            if (argumentResolver instanceof RequestResponseBodyMethodProcessor) {
                customArgumentResolvers.add(new CustomRequestResponseBodyMethodProcessor((RequestResponseBodyMethodProcessor) argumentResolver));
            }
            customArgumentResolvers.add(argumentResolver);
        }

        requestMappingHandlerAdapter.setArgumentResolvers(customArgumentResolvers);

    }
}