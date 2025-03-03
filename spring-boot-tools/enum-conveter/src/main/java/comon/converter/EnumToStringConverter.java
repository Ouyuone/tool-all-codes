package comon.converter;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import comon.enums.DictionaryCode;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;

import java.io.IOException;
import java.util.Objects;

/**
 * Spring MVC 枚举类型装换工厂
 *
 * @author <a href="mailto:yu.ou@alpha-ess.com">欧宇</a>
 * 2024/1/26 14:34
 */
@SuppressWarnings({"rawtypes"})
public class EnumToStringConverter<T extends Enum<T>> extends JsonSerializer<T> implements Converter<T, String> {
    public static final EnumToStringConverter INSTANCE = new EnumToStringConverter();

    private EnumToStringConverter() {
    }

    @Override
    public String convert(@NonNull T source) {
        if (source instanceof DictionaryCode) {
            return String.valueOf(((DictionaryCode) source).getCode());
        }
        return source.name();
    }

    @Override
    public void serialize(T value, JsonGenerator generator, SerializerProvider serializers) throws IOException {
        if (Objects.isNull(value)) {
            generator.writeNull();
        } else {
            generator.writeString(this.convert(value));
        }
    }

    @Override
    public void serializeWithType(T value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        this.serialize(value, gen, serializers);
    }

    @Override
    public Class<T> handledType() {
        return (Class) Enum.class;
    }
}
