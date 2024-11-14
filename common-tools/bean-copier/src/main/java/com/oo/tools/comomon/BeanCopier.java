package com.oo.tools.comomon;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 对象复制工具类
 * 提供了多种复制对象属性的方法，支持嵌套对象、字段映射和自定义复制逻辑
 */
public class BeanCopier {

    /**
     * Bean复制函数接口
     */
    @FunctionalInterface
    public interface BeanCopyConsumer {
        void copy(Object source, Object target, String... ignoreProperties);
    }


    private static final Map<Class<?>, Function<String, ?>> DEFAULT_CONVERTERS = new HashMap<>();
    private static final Map<ConversionKey, TypeConverter<?, ?>> CUSTOM_CONVERTERS = new HashMap<>();
    
    private static final Map<String,Class<?>[]> PRIMITIVE_TYPES = new HashMap<>();


    static {
        DEFAULT_CONVERTERS.put(Boolean.class, Boolean::valueOf);
        DEFAULT_CONVERTERS.put(boolean.class, Boolean::parseBoolean);
        DEFAULT_CONVERTERS.put(Byte.class, Byte::valueOf);
        DEFAULT_CONVERTERS.put(byte.class, Byte::parseByte);
        DEFAULT_CONVERTERS.put(Short.class, Short::valueOf);
        DEFAULT_CONVERTERS.put(short.class, Short::parseShort);
        DEFAULT_CONVERTERS.put(Integer.class, Integer::valueOf);
        DEFAULT_CONVERTERS.put(int.class, Integer::parseInt);
        DEFAULT_CONVERTERS.put(Long.class, Long::valueOf);
        DEFAULT_CONVERTERS.put(long.class, Long::parseLong);
        DEFAULT_CONVERTERS.put(Float.class, Float::valueOf);
        DEFAULT_CONVERTERS.put(float.class, Float::parseFloat);
        DEFAULT_CONVERTERS.put(Double.class, Double::valueOf);
        DEFAULT_CONVERTERS.put(double.class, Double::parseDouble);
        DEFAULT_CONVERTERS.put(Character.class, s -> s.charAt(0));
        DEFAULT_CONVERTERS.put(char.class, s -> s.charAt(0));
        DEFAULT_CONVERTERS.put(String.class, Function.identity());
    }

    static {
        PRIMITIVE_TYPES.put("int", new Class[]{Integer.class,int.class});
        PRIMITIVE_TYPES.put("long", new Class[]{Long.class,long.class});
        PRIMITIVE_TYPES.put("boolean", new Class[]{Boolean.class,boolean.class});
        PRIMITIVE_TYPES.put("short", new Class[]{Short.class,short.class});
        PRIMITIVE_TYPES.put("float", new Class[]{Float.class,float.class});
        PRIMITIVE_TYPES.put("double", new Class[]{Double.class,double.class});
        PRIMITIVE_TYPES.put("char", new Class[]{Character.class,char.class});
     
    }
    /**
     * 默认的Bean复制函数
     */
    private static final BeanCopyConsumer DEFAULT_BEAN_COPY_FUNCTION = (source, target, ignoreProperties) -> {
        Class<?> sourceClass = source.getClass();
        Class<?> targetClass = target.getClass();

        try {
            PropertyDescriptor[] sourceProperties = java.beans.Introspector.getBeanInfo(sourceClass).getPropertyDescriptors();
            PropertyDescriptor[] targetProperties = java.beans.Introspector.getBeanInfo(targetClass).getPropertyDescriptors();

            Set<String> ignoreSet = new HashSet<>(Arrays.asList(ignoreProperties));

            for (PropertyDescriptor sourceProperty : sourceProperties) {
                if (ignoreSet.contains(sourceProperty.getName())) {
                    continue;
                }

                PropertyDescriptor targetProperty = findTargetProperty(targetProperties, sourceProperty.getName());
                if (targetProperty != null) {
                    Method readMethod = sourceProperty.getReadMethod();
                    Method writeMethod = targetProperty.getWriteMethod();
                    if (readMethod != null && writeMethod != null) {
                        Object value = readMethod.invoke(source);
                        //主要解决字段同名但是不同类型的情况,未加注解处理，也没加字段映射map，只加了类型转换器TypeConverter
                         writeMethod.invoke(target,convertValue(value, targetProperty.getPropertyType()));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error copying properties", e);
        }
    };

    /**
     * 查找目标属性
     */
    private static PropertyDescriptor findTargetProperty(PropertyDescriptor[] properties, String name) {
        for (PropertyDescriptor property : properties) {
            if (property.getName().equals(name)) {
                return property;
            }
        }
        return null;
    }

    /**
     * 注册自定义类型转换器
     *
     * @param sourceType 源类型
     * @param targetType 目标类型
     * @param converter  转换器
     * @param <S>        源类型
     * @param <T>        目标类型
     */
    public static <S, T> void registerConverter(Class<S> sourceType, Class<T> targetType, TypeConverter<S, T> converter) {
        CUSTOM_CONVERTERS.put(new ConversionKey(sourceType, targetType), converter);
    }

    /**
     * 复制对象属性
     *
     * @param source 源对象
     * @param target 目标对象
     * @param <S>    源对象类型
     * @param <T>    目标对象类型
     */
    public static <S, T> void copyProperties(S source, T target) {
        copyProperties(source, target, null, null);
    }

    /**
     * 复制对象属性，支持字段映射
     *
     * @param source       源对象
     * @param target       目标对象
     * @param fieldMapping 字段映射
     * @param <S>          源对象类型
     * @param <T>          目标对象类型
     */
    public static <S, T> void copyProperties(S source, T target, Map<String, String> fieldMapping) {
        copyProperties(source, target, null, fieldMapping);
    }

    /**
     * 复制对象属性，支持自定义复制逻辑、字段映射和忽略属性
     *
     * @param source           源对象
     * @param target           目标对象
     * @param customCopyLogic  自定义复制逻辑
     * @param fieldMapping     字段映射
     * @param ignoreProperties 忽略的属性
     * @param <S>              源对象类型
     * @param <T>              目标对象类型
     */
    public static <S, T> void copyProperties(S source, T target, BiConsumer<S, T> customCopyLogic, Map<String, String> fieldMapping
        , String... ignoreProperties) {
        copyProperties(source, target, customCopyLogic, fieldMapping, DEFAULT_BEAN_COPY_FUNCTION, ignoreProperties);
    }

    /**
     * 复制对象属性，支持自定义复制逻辑、字段映射和忽略属性
     *
     * @param source           源对象
     * @param target           目标对象
     * @param customCopyLogic  自定义复制逻辑
     * @param fieldMapping     字段映射
     * @param ignoreProperties 忽略的属性
     * @param <S>              源对象类型
     * @param <T>              目标对象类型
     */
    public static <S, T> void copyProperties(S source, T target, BiConsumer<S, T> customCopyLogic, Map<String, String> fieldMapping
        , BeanCopyConsumer beanCopyConsumer, String... ignoreProperties) {
        if (source == null || target == null) {
            throw new IllegalArgumentException("源对象和目标对象不能为空");
        }
        // 使用默认的Bean复制函数
        (beanCopyConsumer != null ? beanCopyConsumer : DEFAULT_BEAN_COPY_FUNCTION).copy(source, target, ignoreProperties);

        // 处理手动字段映射
        if (fieldMapping != null && !fieldMapping.isEmpty()) {
            handleManualFieldMapping(source, target, fieldMapping);
        }

        // 处理注解映射
        handleAnnotationMapping(source, target);


        if (customCopyLogic != null) {
            customCopyLogic.accept(source, target);
        }
    }

    /**
     * 处理字段映射
     *
     * @param source 源对象
     * @param target 目标对象
     * @param sourceField 源字段名
     * @param targetField 目标字段名
     */
    private static void handleFieldMapping(Object source, Object target, String sourceField, String targetField) {
        try {
            Field sourceFieldObj = source.getClass().getDeclaredField(sourceField);
            Field targetFieldObj = target.getClass().getDeclaredField(targetField);
            sourceFieldObj.setAccessible(true);
            targetFieldObj.setAccessible(true);

            Object value = sourceFieldObj.get(source);
            Object convertedValue = convertValue(value, targetFieldObj.getType());
            targetFieldObj.set(target, convertedValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Error mapping field " + sourceField + " to " + targetField, e);
        }
    }

    /**
     * 处理手动字段映射
     *
     * @param source 源对象
     * @param target 目标对象
     * @param fieldMapping 字段映射
     */
    private static <S, T> void handleManualFieldMapping(S source, T target, Map<String, String> fieldMapping) {
        for (Map.Entry<String, String> entry : fieldMapping.entrySet()) {
            String targetField = entry.getKey();
            String sourceField = entry.getValue();
            handleFieldMapping(source, target, sourceField, targetField);
        }
    }

    /**
     * 处理注解映射
     *
     * @param source 源对象
     * @param target 目标对象
     */
    private static <S, T> void handleAnnotationMapping(S source, T target) {
        Class<?> targetClass = target.getClass();

        for (Field field : targetClass.getDeclaredFields()) {
            FieldMapping annotation = field.getAnnotation(FieldMapping.class);
            if (annotation != null && annotation.targetClass().isAssignableFrom(source.getClass())) {
                String sourceField = annotation.source();
                String targetField = field.getName();
                handleFieldMapping(source, target, sourceField, targetField);
            }
        }
    }

    /**
     * 创建对象复制函数
     *
     * @param targetClass     目标类
     * @param customCopyLogic 自定义复制逻辑
     * @param <S>             源对象类型
     * @param <T>             目标对象类型
     * @return 复制函数
     */
    public static <S, T> Function<S, T> createCopyFunction(Class<T> targetClass, BiConsumer<S, T> customCopyLogic) {
        return source -> {
            try {
                T target = targetClass.getDeclaredConstructor().newInstance();
                copyProperties(source, target, customCopyLogic, null);
                return target;
            } catch (Exception e) {
                throw new RuntimeException("创建目标实例时出错", e);
            }
        };
    }

    /**
     * 复制对象列表
     *
     * @param sourceList  源对象列表
     * @param targetClass 目标类
     * @param <S>         源对象类型
     * @param <T>         目标对象类型
     * @return 复制后的对象列表
     */
    public static <S, T> List<T> copyList(Collection<S> sourceList, Class<T> targetClass) {
        return copyList(sourceList, targetClass, null);
    }

    /**
     * 复制对象列表，支持自定义复制逻辑
     *
     * @param sourceList      源对象列表
     * @param targetClass     目标类
     * @param customCopyLogic 自定义复制逻辑
     * @param <S>             源对象类型
     * @param <T>             目标对象类型
     * @return 复制后的对象列表
     */
    public static <S, T> List<T> copyList(Collection<S> sourceList, Class<T> targetClass, BiConsumer<S, T> customCopyLogic) {
        if (sourceList == null) {
            return new ArrayList<>();
        }
        return sourceList.stream()
            .map(createCopyFunction(targetClass, customCopyLogic))
            .collect(Collectors.toList());
    }

    /**
     * 复制属性，包括嵌套对象
     *
     * @param source 源对象
     * @param target 目标对象
     * @param <S>    源对象类型
     * @param <T>    目标对象类型
     */
    public static <S, T> void copyPropertiesWithNestedObjects(S source, T target) {
        copyProperties(source, target);
        handleNestedObjects(source, target);
    }

    /**
     * 处理嵌套对象的复制
     *
     * @param source 源对象
     * @param target 目标对象
     * @param <S>    源对象类型
     * @param <T>    目标对象类型
     */
    private static <S, T> void handleNestedObjects(S source, T target) {
        BeanWrapper targetWrapper = new BeanWrapperImpl(target);
        for (PropertyDescriptor descriptor : targetWrapper.getPropertyDescriptors()) {
            String propertyName = descriptor.getName();
            if (targetWrapper.isWritableProperty(propertyName)) {
                Class<?> propertyType = descriptor.getPropertyType();
                if (isComplexType(propertyType) && targetWrapper.getPropertyValue(propertyName) == null) {
                    try {
                        Object nestedObject = propertyType.getDeclaredConstructor().newInstance();
                        copyProperties(source, nestedObject);
                        targetWrapper.setPropertyValue(propertyName, nestedObject);
                    } catch (Exception e) {
                        throw new RuntimeException("创建嵌套对象失败，属性: " + propertyName, e);
                    }
                }
            }
        }
    }

    /**
     * 转换值的类型
     *
     * @param value      原始值
     * @param targetType 目标类型
     * @return 转换后的值
     */
    private static <S, T> Object convertValue(S value, Class<T> targetType) {
        if (value == null) {
            return null;
        }
        
       
        if (targetType.isAssignableFrom(value.getClass())) {
            return targetType.cast(value);
        }
        
        //这里是取出基础类型映射的包装类型，有可能字段一样但是类型不一样就会走到这里
        Class<T>[] classes = (Class<T>[]) PRIMITIVE_TYPES.get(targetType.getName());
        if (classes != null) {
            for (Class<T> tClass : classes) {
                targetType = tClass;
                if (targetType.isAssignableFrom(value.getClass())) {
                    return targetType.cast(value);
                }
            }
        }

        // Attempt to use custom converter
        @SuppressWarnings("unchecked")
        TypeConverter<Object, T> customConverter = (TypeConverter<Object, T>) getCustomConverter(value.getClass(), targetType);
        if (customConverter != null) {
            return customConverter.convert(value);
        }

        // 尝试使用默认转换器
        Function<String, ?> defaultConverter = DEFAULT_CONVERTERS.get(targetType);
        if (defaultConverter != null) {
            return defaultConverter.apply(value.toString());
        }

        if (targetType.isEnum()) {
            @SuppressWarnings("unchecked")
            T enumValue = (T) Enum.valueOf((Class<? extends Enum>) targetType, value.toString());
            return enumValue;
        }

        if (isComplexType(targetType)) {
            try {
                Object nestedTarget = targetType.getDeclaredConstructor().newInstance();
                copyProperties(value, nestedTarget);
                return nestedTarget;
            } catch (Exception e) {
                throw new RuntimeException("Failed to create instance of " + targetType.getName(), e);
            }
        }

        throw new IllegalArgumentException("无法将 " + value.getClass().getSimpleName() + " 转换为 " + targetType.getSimpleName());
    }

    /**
     * 判断是否为复杂类型
     *
     * @param type 类型
     * @return 是否为复杂类型
     */
    private static boolean isComplexType(Class<?> type) {
        return !type.isPrimitive() && !type.equals(String.class) && !Number.class.isAssignableFrom(type) && !type.isEnum();
    }

    private static class ConversionKey {
        private final Class<?> sourceType;
        private final Class<?> targetType;

        public ConversionKey(Class<?> sourceType, Class<?> targetType) {
            this.sourceType = sourceType;
            this.targetType = targetType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ConversionKey that = (ConversionKey) o;
            return Objects.equals(sourceType, that.sourceType) &&
                Objects.equals(targetType, that.targetType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(sourceType, targetType);
        }
    }

    @SuppressWarnings("unchecked")
    private static <S, T> TypeConverter<S, T> getCustomConverter(Class<S> sourceType, Class<T> targetType) {
        return (TypeConverter<S, T>) CUSTOM_CONVERTERS.get(new ConversionKey(sourceType, targetType));
    }
}
