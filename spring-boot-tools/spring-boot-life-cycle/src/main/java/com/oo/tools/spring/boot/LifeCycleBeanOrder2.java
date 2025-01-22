package com.oo.tools.spring.boot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

@Slf4j
public class LifeCycleBeanOrder2 implements
        BeanPostProcessor,
        BeanFactoryPostProcessor {
   @Override
   public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
      if(bean instanceof LifeCycleBean){
         log.error("启动顺序:BeanPostProcessor postProcessBeforeInitialization beanName:{}", beanName);
         
      }
      return bean;
   }

   @Override
   public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
      if(bean instanceof LifeCycleBean){
         log.error("启动顺序:BeanPostProcessor postProcessAfterInitialization beanName:{}", beanName);
         
      }
      return bean;
   }

   @Override
   public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
         log.error("启动顺序:BeanFactoryPostProcessor postProcessBeanFactory ");
   }
}