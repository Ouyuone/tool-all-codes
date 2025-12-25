package com.oo.tools.spring.boot.typehandler;


import com.oo.tools.spring.boot.converter.CodeToEnumConverterFactory;
import com.oo.tools.spring.boot.enums.DictionaryCode;
import com.oo.tools.spring.boot.enums.DictionaryName;
import com.oo.tools.spring.boot.exception.TypeConverterHandlerException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

import java.lang.reflect.Type;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * 默认的 枚举转换器
 * @param <E>
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
public class EnumToCodeForStringTypeHandler<E extends Enum<?>> extends BaseTypeHandler<Enum<?>> {

    private final Class<E> genericType;

    public EnumToCodeForStringTypeHandler() {
        Type type = getRawType();
        if (type instanceof Class clazz && Enum.class.isAssignableFrom(clazz)) {
            this.genericType = clazz;
        } else {
            throw new TypeConverterHandlerException("Unknown generic type");
        }
    }

    public EnumToCodeForStringTypeHandler(Class<E> type) {
        this.genericType = type;
    }
    
    
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Enum<?> parameter, JdbcType jdbcType) throws SQLException {
        String value = "";
        if(parameter instanceof DictionaryCode<?> dictionaryCode){
            value = dictionaryCode.getCode().toString();
        }else if (parameter instanceof DictionaryName dictionaryName){
            value = dictionaryName.getName();
        }
        ps.setObject(i, value, Objects.isNull(jdbcType) ? JdbcType.VARCHAR.TYPE_CODE : jdbcType.TYPE_CODE);
    }
    
    @Override
    public Enum<?> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return CodeToEnumConverterFactory.INSTANCE.getConverter(this.genericType).convert(rs.getString(columnName));
    }
    
    @Override
    public Enum<?> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return CodeToEnumConverterFactory.INSTANCE.getConverter(this.genericType).convert(rs.getString(columnIndex));
    }
    
    @Override
    public Enum<?> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return CodeToEnumConverterFactory.INSTANCE.getConverter(this.genericType).convert(cs.getString(columnIndex));
    }
    
}
