package com.oo.tools.spring.boot.method.argument.resolver;

import com.oo.tools.spring.boot.common.DefaultPageRequest;
import com.oo.tools.spring.boot.support.CheckRequestHandle;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date 2024/2/22 14:31
 */

public class PageableServletModelAttributeMethodProcessor implements HandlerMethodArgumentResolver {
    private static final SortHandlerMethodArgumentResolver DEFAULT_SORT_RESOLVER = new SortHandlerMethodArgumentResolver();

    private ServletModelAttributeMethodProcessor servletModelAttributeMethodProcessor;
    private PageableHandlerMethodArgumentResolver pageableHandlerMethodArgumentResolver;

    /**
     * Class constructor.
     *
     * @param annotationNotRequired if "true", non-simple method arguments and
     *                              return values are considered model attributes with or without a
     *                              {@code @ModelAttribute} annotation
     */
    public PageableServletModelAttributeMethodProcessor(boolean annotationNotRequired) {
        this(annotationNotRequired, (SortArgumentResolver) null);
    }

    public PageableServletModelAttributeMethodProcessor(boolean annotationNotRequired, SortHandlerMethodArgumentResolver sortResolver) {
        this(annotationNotRequired, (SortArgumentResolver) sortResolver);
    }

    public PageableServletModelAttributeMethodProcessor(boolean annotationNotRequired, SortArgumentResolver sortResolver) {
        this(new ServletModelAttributeMethodProcessor(annotationNotRequired), new PageableHandlerMethodArgumentResolver(sortResolver));
    }

    public PageableServletModelAttributeMethodProcessor(ServletModelAttributeMethodProcessor servletModelAttributeMethodProcessor,
                                                        PageableHandlerMethodArgumentResolver pageableHandlerMethodArgumentResolver) {
        this.servletModelAttributeMethodProcessor = servletModelAttributeMethodProcessor;
        this.pageableHandlerMethodArgumentResolver = pageableHandlerMethodArgumentResolver;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return DefaultPageRequest.class.isAssignableFrom(parameter.getParameterType()) || this.servletModelAttributeMethodProcessor.supportsParameter(parameter);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Object result = this.servletModelAttributeMethodProcessor.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        if (result instanceof DefaultPageRequest pageRequest) {
            Pageable pageable = this.pageableHandlerMethodArgumentResolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
            pageRequest.setPageable(pageable);
        }
   
        return CheckRequestHandle.checkRequestHandler(result);
    }


}