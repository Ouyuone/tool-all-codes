package com.oo.tools.spring.boot.types;

import org.springframework.lang.NonNull;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2024/06/27 11:27:51
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
@Documented
public @interface DynamicSwitch {

    /**
     * 用来保存需要切换的数据源名称
     * @return
     */
    @NonNull
    String value() default "master";
}
