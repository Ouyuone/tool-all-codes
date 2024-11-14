package com.oo.tools.comomon;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FieldMapping {
    /**
     * 源属性名
     */
    String source();

    /**
     * 目标类
     */
    Class<?> targetClass();
}
