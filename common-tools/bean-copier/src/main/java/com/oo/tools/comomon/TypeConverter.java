package com.oo.tools.comomon;

/**
 * 类型转换器接口
 */
public interface TypeConverter<S, T> {
    /**
     * 将源类型转换为目标类型
     * @param source 源对象
     * @return 转换后的目标对象
     */
    T convert(S source);
}
