package com.oo.tools.spring.boot.supports.annotation;

import com.oo.tools.spring.boot.types.DatabaseExecuteType;
import jakarta.annotation.Nullable;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2024/06/27 18:30:05
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
@Documented
public @interface DynamicSwitchRouter {

    /**
     * 主从数据源选择
     * @return
     */
    String value() default "master";


    @AliasFor(value = "value")
    String dynamicDB() default "master";

    /**
     * 路由策略，分表标记
     * @return 是否需要分表
     */
    boolean splitTable() default false;


    /**
     * 分库分表字段
     * @return
     */
    String routerKey() default "";
}
