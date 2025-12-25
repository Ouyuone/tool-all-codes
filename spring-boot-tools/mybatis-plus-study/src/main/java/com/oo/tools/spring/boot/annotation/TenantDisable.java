package com.oo.tools.spring.boot.annotation;

import java.lang.annotation.*;

/**
 * TenantDisable
 * 用于标记不需要进行租户过滤的实体类
 *
 * @author Yu.ou
 * @version 1.0.0
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TenantDisable {
} 