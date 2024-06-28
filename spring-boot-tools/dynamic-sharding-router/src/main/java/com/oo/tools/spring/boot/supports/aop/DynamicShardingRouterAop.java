package com.oo.tools.spring.boot.supports.aop;

import com.oo.tools.spring.boot.domain.service.impl.DefaultDBRouterStrategy;
import com.oo.tools.spring.boot.supports.DBContextHolder;
import com.oo.tools.spring.boot.supports.DBRouterConfig;
import com.oo.tools.spring.boot.supports.annotation.DynamicSwitchRouter;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2024/06/28 13:58:59
 */
@Aspect
public class DynamicShardingRouterAop {

    private static final Logger logger = LoggerFactory.getLogger(DynamicShardingRouterAop.class);

    private final Map<String, DBRouterConfig> dbRouterConfigMap;

    private final DefaultDBRouterStrategy dbRouterStrategy;

    private final Map<String,Boolean> shardingRouterMap;

    public DynamicShardingRouterAop(Map<String, DBRouterConfig> dbRouterConfigMap, DefaultDBRouterStrategy dbRouterStrategy,Map<String,Boolean> shardingRouterMap) {
        this.dbRouterConfigMap = dbRouterConfigMap;
        this.dbRouterStrategy = dbRouterStrategy;
        this.shardingRouterMap = shardingRouterMap;
    }

    @Pointcut("@annotation(com.oo.tools.spring.boot.supports.annotation.DynamicSwitchRouter) || @within(com.oo.tools.spring.boot.supports.annotation.DynamicSwitchRouter)")
    public void execute() {
    }


    @Around("execute()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        Method method = signature.getMethod();

        DynamicSwitchRouter dynamicSwitchRouter = AnnotationUtils.findAnnotation(method, DynamicSwitchRouter.class);

        if (Objects.isNull(dynamicSwitchRouter)) {
            dynamicSwitchRouter = AnnotationUtils.findAnnotation(method.getDeclaringClass(), DynamicSwitchRouter.class);
            if (Objects.isNull(dynamicSwitchRouter))
                return joinPoint.proceed();
        }

        logger.info("方法:{} 进入动态切换数据源切面。。。",method.getDeclaringClass().getName()+"#"+method.getName());
        // 获取注解上的属性值
        String dynamicDBKey = dynamicSwitchRouter.value();
        DBRouterConfig dbRouterConfig = dbRouterConfigMap.get(dynamicDBKey);
        if (Objects.isNull(dbRouterConfig)) {
            String message = String.format("method:{%s},添加注解设置的dynamicDBKey:{}未找到对应的DBRouterConfig", method.getDeclaringClass().getName() + "#" + method.getName(), dynamicDBKey);
            throw new RuntimeException(message);
        }

        //分片
        boolean splitTable = dynamicSwitchRouter.splitTable();


        if (shardingRouterMap.get(dynamicDBKey) && splitTable) {

            String routerKey = StringUtils.isBlank(dynamicSwitchRouter.routerKey()) ? dbRouterConfig.getRouterKey() : dynamicSwitchRouter.routerKey();

            // 路由属性
            String dbKeyAttr = getAttrValue(routerKey, joinPoint.getArgs());
            dbRouterStrategy.doRouter(dbKeyAttr, dynamicDBKey);

        } else {
            //不分片
            dbRouterStrategy.doRouter(null, dynamicDBKey);
        }
        logger.info("方法:{} 切换到数据源:{}。。。分表切换Key:{}", method.getDeclaringClass().getName()+"#"+ method.getName(), DBContextHolder.getDBKey(),DBContextHolder.getTBKey());

        // 返回结果
        try {
            return joinPoint.proceed();
        } finally {
            logger.info("方法:{} 退出动态切换数据源切面。。。",method.getDeclaringClass().getName()+"#"+method.getName());
            dbRouterStrategy.clear();
        }
    }


    public String getAttrValue(String attr, Object[] args) {
        if (1 == args.length) {
            Object arg = args[0];
            if (arg instanceof String) {
                return arg.toString();
            }
            if (arg instanceof Object) {
                MetaObject metaObject = SystemMetaObject.forObject(arg);
                if (metaObject.hasGetter(attr)) {
                    return metaObject.getValue(attr).toString();
                }
            }
        }

        String filedValue = null;
        for (Object arg : args) {
            try {
                if (StringUtils.isNotBlank(filedValue)) {
                    break;
                }
                // filedValue = BeanUtils.getProperty(arg, attr);
                // fix: 使用lombok时，uId这种字段的get方法与idea生成的get方法不同，会导致获取不到属性值，改成反射获取解决
                filedValue = String.valueOf(this.getValueByName(arg, attr));
            } catch (Exception e) {
                logger.error("获取路由属性值失败 attr：{}", attr, e);
            }
        }
        return filedValue;
    }

    /**
     * 获取对象的特定属性值
     *
     * @param item 对象
     * @param name 属性名
     * @return 属性值
     * @author tang
     */
    private Object getValueByName(Object item, String name) {
        try {
            Field field = getFieldByName(item, name);
            if (field == null) {
                return null;
            }
            field.setAccessible(true);
            Object o = field.get(item);
            field.setAccessible(false);
            return o;
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    /**
     * 根据名称获取方法，该方法同时兼顾继承类获取父类的属性
     *
     * @param item 对象
     * @param name 属性名
     * @return 该属性对应方法
     * @author tang
     */
    private Field getFieldByName(Object item, String name) {
        try {
            Field field;
            try {
                field = item.getClass().getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                field = item.getClass().getSuperclass().getDeclaredField(name);
            }
            return field;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }
}
