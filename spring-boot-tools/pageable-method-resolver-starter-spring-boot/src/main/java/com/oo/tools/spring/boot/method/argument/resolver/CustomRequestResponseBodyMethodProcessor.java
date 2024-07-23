package com.oo.tools.spring.boot.method.argument.resolver;

import com.oo.tools.spring.boot.support.CheckRequestHandle;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2024/02/19 17:48:07
 */
public class CustomRequestResponseBodyMethodProcessor implements HandlerMethodArgumentResolver {
    private RequestResponseBodyMethodProcessor requestResponseBodyMethodProcessor;

    public CustomRequestResponseBodyMethodProcessor(RequestResponseBodyMethodProcessor requestResponseBodyMethodProcessor) {
        this.requestResponseBodyMethodProcessor = requestResponseBodyMethodProcessor;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return requestResponseBodyMethodProcessor.supportsParameter(parameter);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Object result = requestResponseBodyMethodProcessor.resolveArgument(parameter, mavContainer, webRequest, binderFactory);

        return CheckRequestHandle.checkRequestHandler(result);
    }
}