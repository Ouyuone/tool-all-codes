package com.oo.tools.spring.boot;

import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 在指定包下扫面标志类,将其加载到Spring上下文中 * @author chenmin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(BeansControlScannerRegistrar.class)
public @interface BeansLoader {
    /**
     * 包扫描路径(不填时从当前路径下扫描)     * @return
     */
    String[] basePackages() default {};
    
    /**
     * 自定义Bean名称生成策略     * @return
     */
    Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;
    
    /**
     * 加载指定注解的类     * @return
     */
    Class<? extends Annotation>[] annotationClass() default {};
    
    /**
     * 标识类     * @return
     */
    Class<?>[] markerInterfaces() default {};
    
    String[] beanExpressions() default {};
}