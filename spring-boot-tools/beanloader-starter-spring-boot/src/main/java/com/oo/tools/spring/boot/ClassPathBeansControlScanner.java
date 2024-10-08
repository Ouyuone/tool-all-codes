/**
 * $Id: ClassPathBeansControlScanner.java,v 1.0 2019-07-27 14:48 chenmin Exp $
 */
package com.oo.tools.spring.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.util.ObjectUtils;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author 陈敏 * @version $Id: ClassPathBeansControlScanner.java,v 1.1 2019-07-27 14:48 chenmin Exp $ * Created on 2019-07-27 14:48 * My blog： https://www.chenmin.info
 */
//@Accessors(chain = true)
public class ClassPathBeansControlScanner extends ClassPathBeanDefinitionScanner {
//    @Setter
    private Class<?>[] annotationClasses;
//    @Setter
    private Class<?>[] markerInterfaces;
    
    private Pattern[] beanExpressionPatterns;
    
    public void setAnnotationClasses(Class<?>[] annotationClasses) {
        this.annotationClasses = annotationClasses;
    }
    
    public void setMarkerInterfaces(Class<?>[] markerInterfaces) {
        this.markerInterfaces = markerInterfaces;
    }
    
    private final Logger log = LoggerFactory.getLogger(ClassPathBeansControlScanner.class);
    
    public ClassPathBeansControlScanner setBeanExpressions(String[] beanExpressions) {
        if (!ObjectUtils.isEmpty(beanExpressions)) {
            this.beanExpressionPatterns = new Pattern[beanExpressions.length];
            for (int i = 0; i < beanExpressions.length; i++) {
                beanExpressionPatterns[i] = Pattern.compile(beanExpressions[i]);
            }
        }
        return this;
    }
    
    public ClassPathBeansControlScanner(BeanDefinitionRegistry registry) {
        super(registry, false);
    }
    
    /**
     * 配置扫描接口     * 扫描添加了markerInterfaces标志类的类或标注了annotationClass注解的类,     * 或者扫描所有类
     */
    public void registerFilters() {        /**         * 如果指定注解,则筛选标注了指定注解的类         */
        if (annotationClasses != null && !ObjectUtils.isEmpty(annotationClasses)) {
            for (Class<?> annotationClass : annotationClasses) {
//                addIncludeFilter(new MyAnnotationTypeFilter((Class<? extends Annotation>) annotationClass));
                addIncludeFilter(new AnnotationTypeFilter((Class<? extends Annotation>) annotationClass));
            }
        }        /**         * 如果指定接口,则筛选标注了指定接口的类         */if (markerInterfaces != null) {
            for (Class<?> markerInterface : markerInterfaces) {
                addIncludeFilter(new AssignableTypeFilter(markerInterface));
            }
        }        /**         * 按照Bean类名判定是否满足正则表达式来加载         */
        if (!ObjectUtils.isEmpty(beanExpressionPatterns)) {
            for (Pattern beanExpressionPattern : beanExpressionPatterns) {
                addIncludeFilter(new RegexPatternTypeFilter(beanExpressionPattern));
            }
        }
    }
    
    /**
     * 重写ClassPathBeanDefinitionScanner的doScan方法,以便在我们自己的逻辑中调用     *     * @param basePackages     * @return
     */
    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        if (beanDefinitions.isEmpty()) {
            log.warn("No Jfinal Controller was found in '" + Arrays.toString(basePackages) + "' package. Please check your configuration.");
        }
        return beanDefinitions;
    }
    
    /**
     * 判断bean是否满足条件,可以被加载到Spring中,markerInterfaces标志类功能再此处实现     *     * @param beanDefinition     * @return true: 可以被加载到Spring中
     */
    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        for (Class<?> markerInterface : markerInterfaces) {
            if (checkSuperClass(markerInterface, beanDefinition) || checkInterface(markerInterface, beanDefinition) /*|| checkClass(markerInterface, beanDefinition) */ || checkBeanExpression(beanDefinition)) {
                return true;
            }
        }
        
        for (Class<?> annotationClass : annotationClasses) {
            boolean hasAnnotation = beanDefinition.getMetadata().hasAnnotation(annotationClass.getName());
            
            if (hasAnnotation) {
                return true;
            }
        }
        return false;
    }
    
    private boolean checkSuperClass(Class<?> markerInterface, AnnotatedBeanDefinition beanDefinition) {
        return markerInterface.getName().equals(beanDefinition.getMetadata().getSuperClassName());
    }
    
    private boolean checkInterface(Class<?> markerInterface, AnnotatedBeanDefinition beanDefinition) {
        String[] interfaceNames = beanDefinition.getMetadata().getInterfaceNames();
        for (String interfaceName : interfaceNames) {
            if (markerInterface.getName().equals(interfaceName)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean checkClass(Class<?> markerInterface, AnnotatedBeanDefinition beanDefinition) {
        return markerInterface.getName().equals(beanDefinition.getMetadata().getClassName());
    }
    
    private boolean checkBeanExpression(AnnotatedBeanDefinition beanDefinition) {
        if (Objects.isNull(beanExpressionPatterns)) {
            return false;
        }
        
        for (Pattern beanExpressionPattern : beanExpressionPatterns) {
            if (beanExpressionPattern.matcher(beanDefinition.getMetadata().getClassName()).matches()) {
                return true;
            }
        }
        return false;
    }
}