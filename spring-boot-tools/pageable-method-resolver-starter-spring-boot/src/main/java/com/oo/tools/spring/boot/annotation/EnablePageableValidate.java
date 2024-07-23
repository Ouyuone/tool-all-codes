package com.oo.tools.spring.boot.annotation;

import com.oo.tools.spring.boot.config.HandlerMethodArgumentResolverAutoConfig;
import com.oo.tools.spring.boot.config.WebConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 启用分页和验证
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2024/07/23 11:43:34
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.TYPE})
@Documented
@Import({WebConfig.class, HandlerMethodArgumentResolverAutoConfig.class})
public @interface EnablePageableValidate {
}
