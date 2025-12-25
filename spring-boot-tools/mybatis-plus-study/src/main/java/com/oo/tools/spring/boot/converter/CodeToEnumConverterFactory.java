package com.oo.tools.spring.boot.converter;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.oo.tools.spring.boot.enums.DictionaryCode;
import com.oo.tools.spring.boot.enums.DictionaryName;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Spring MVC 枚举类型装换工厂
 *
 * @author <a href="mailto:yu.ou@alpha-ess.com">欧宇</a>
 * 2024/1/26 14:34
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class CodeToEnumConverterFactory implements ConverterFactory<String, Enum> {
    public static final CodeToEnumConverterFactory INSTANCE = new CodeToEnumConverterFactory();
    private static final Map<Class<? extends Enum>, Converter<String, Enum<? extends Enum>>>
            CODE_TO_ENUM_CONVERTER_MAP = new ConcurrentHashMap<>();

    private CodeToEnumConverterFactory() {
    }

    @Override
    public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
        Converter converter = CODE_TO_ENUM_CONVERTER_MAP.get(targetType);
        if (converter == null) {
            converter = new CodeToEnumConverter<>(targetType);
            CODE_TO_ENUM_CONVERTER_MAP.put(targetType, converter);
        }
        return converter;
    }
    
    static class CodeToEnumConverter<T extends Enum<T>> extends JsonDeserializer<T>  implements Converter<String, T> {
        private final Class<T> enumType;
        private final Map<String, T> enumMap = new HashMap<>();

        private CodeToEnumConverter(Class<T> enumType) {
            this.enumType = enumType;
            for (T e : enumType.getEnumConstants()) {
                if (e instanceof DictionaryCode code) {
                    if (Objects.nonNull(code.getCode())) {
                        String codeValue = String.valueOf(code.getCode());
                        this.enumMap.put("code@" + codeValue.toLowerCase(Locale.ROOT), e);
                        if (StringUtils.containsAny(codeValue, " ", "/", "\\")) {
                            this.enumMap.put("code@escape@" + StringUtils.replaceEach(codeValue, new String[]{" ", "/", "\\"}, new String[]{"-", "-", "-"}).toLowerCase(Locale.ROOT), e);
                        }
                    }
                }
                if (e instanceof DictionaryName name) {
                    if (StringUtils.isNotBlank(name.getName())) {
                        this.enumMap.put("name@" + name.getName().toLowerCase(Locale.ROOT), e);
                    }
                }
                this.enumMap.put("enumName@" + e.name().toLowerCase(Locale.ROOT), e);
            }
        }

        @Override
        public T convert(String source) {
            if (StringUtils.isBlank(source)) {
                return null;
            }
            source = source.toLowerCase(Locale.ROOT);
            T result = this.enumMap.get("code@" + source);
            if (Objects.nonNull(result)) {
                return result;
            }
            result = this.enumMap.get("code@escape@" + source);
            if (Objects.nonNull(result)) {
                return result;
            }
            result = this.enumMap.get("name@" + source);
            if (Objects.nonNull(result)) {
                return result;
            }
            result = this.enumMap.get("enumName@" + source);
            if (Objects.nonNull(result)) {
                return result;
            }
            throw new IllegalArgumentException("No element matches " + source + " in " + this.enumType.getName());
        }

        @Override
        public T deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
            return this.convert(jsonParser.getText());
        }
    }
}
