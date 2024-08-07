/**
 * $Id: BeansControlScannerRegistrar.java,v 1.0 2019-07-27 14:57 chenmin Exp $
 */
package com.oo.tools.spring.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 陈敏 * @version $Id: BeansControlScannerRegistrar.java,v 1.1 2019-07-27 14:57 chenmin Exp $ * Created on 2019-07-27 14:57 * My blog： https://www.chenmin.info
 */
public class BeansControlScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware, BeanFactoryAware {
    private ResourceLoader resourceLoader;
    private Environment environment;
    private BeanFactory beanFactory;
    
    private final Logger log = LoggerFactory.getLogger(BeansControlScannerRegistrar.class);
    
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(BeansLoader.class.getName()));
        ClassPathBeansControlScanner scanner = new ClassPathBeansControlScanner(registry);
        if (resourceLoader != null) {
            scanner.setResourceLoader(resourceLoader);
        }
        Class<?>[] annotationClasses = annoAttrs.getClassArray("annotationClass");
        if (!Annotation.class.equals(annotationClasses)) {
            scanner.setAnnotationClasses(annotationClasses);
        }
        Class<?>[] markerInterfaces = annoAttrs.getClassArray("markerInterfaces");
        if (!Class.class.equals(markerInterfaces)) {
            scanner.setMarkerInterfaces(markerInterfaces);
        }
        Class<? extends BeanNameGenerator> generatorClass = annoAttrs.getClass("nameGenerator");
        if (!BeanNameGenerator.class.equals(generatorClass)) {
            scanner.setBeanNameGenerator(BeanUtils.instantiateClass(generatorClass));
        }
        String[] beanExpressions = annoAttrs.getStringArray("beanExpressions");
        if (!ObjectUtils.isEmpty(beanExpressions)) {
            scanner.setBeanExpressions(beanExpressions);
        }
        List<String> packages = new ArrayList<String>();
        String[] basePackages = annoAttrs.getStringArray("basePackages");
        if (ObjectUtils.isEmpty(basePackages)) {
            String basepackage = ((StandardAnnotationMetadata) importingClassMetadata).getIntrospectedClass().getPackage().getName();
//            packages.addAll(AutoConfigurationPackages.get(this.beanFactory));
            packages.addAll(Arrays.asList(basepackage));
        } else {
            packages.addAll(Arrays.asList(basePackages));
        }
        scanner.registerFilters();
        scanner.doScan(StringUtils.toStringArray(packages));
    }
    
    
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        
    }
    
    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
    
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}