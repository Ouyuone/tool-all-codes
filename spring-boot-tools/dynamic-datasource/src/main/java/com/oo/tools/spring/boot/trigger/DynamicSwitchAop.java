package com.oo.tools.spring.boot.trigger;

import com.oo.tools.spring.boot.support.DynamicSwitchContext;
import com.oo.tools.spring.boot.types.DynamicDatasourceLeveType;
import com.oo.tools.spring.boot.types.DynamicSwitch;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;

/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2024/06/27 11:30:44
 */
@Aspect
public class DynamicSwitchAop {
    private static final Logger logger = LoggerFactory.getLogger(DynamicSwitchAop.class);

    @Pointcut("@annotation(com.oo.tools.spring.boot.types.DynamicSwitch) || @within(com.oo.tools.spring.boot.types.DynamicSwitch)")
    public void executable() {}

    @Around("executable()")
    public Object dynamicSwitch(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature)pjp.getSignature();
        Method method = signature.getMethod();
        logger.info("方法:{} 进入动态切换数据源切面。。。",method.getDeclaringClass().getName()+"#"+method.getName());

        dynamicContextSwitch(method);

        Object obj = pjp.proceed();

        logger.info("方法:{} 退出动态切换数据源切面。。。",method.getDeclaringClass().getName()+"#"+method.getName());
        DynamicSwitchContext.clearContext();
        return obj;
    }

    private static void dynamicContextSwitch(Method method) {
        String contextSwitch;
        DynamicSwitch dynamicSwitch = AnnotationUtils.findAnnotation(method, DynamicSwitch.class);
        if (ObjectUtils.isEmpty(dynamicSwitch)) {
            dynamicSwitch = AnnotationUtils.findAnnotation(method.getDeclaringClass(), DynamicSwitch.class);
        }
        contextSwitch = dynamicSwitch.value();
        DynamicSwitchContext.setContextSwitch(contextSwitch);
        logger.info("方法:{} 切换到数据源:{}。。。", method.getDeclaringClass().getName()+"#"+ method.getName(),contextSwitch);
    }

}
