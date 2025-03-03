package comon.configurations;



import comon.converter.CodeToEnumConverterFactory;
import comon.converter.EnumToStringConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">欧宇</a>
 * 2025/2/26 17:35
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(EnumToStringConverter.INSTANCE);
        registry.addConverterFactory(CodeToEnumConverterFactory.INSTANCE);
    }

}
