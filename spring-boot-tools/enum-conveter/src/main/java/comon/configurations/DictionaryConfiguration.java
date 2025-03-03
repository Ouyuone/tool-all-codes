package comon.configurations;



import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import comon.converter.CodeToEnumConverterFactory;
import comon.converter.EnumToStringConverter;
import comon.enums.DictionaryCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">欧宇</a>
 * 2025/2/26 17:02
 */
@Slf4j
@Component
@SuppressWarnings("all")
@RequiredArgsConstructor
public class DictionaryConfiguration implements ApplicationListener<ContextRefreshedEvent> {

    private final ObjectMapper objectMapper;
    private final SimpleModule simpleModule = new SimpleModule();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        this.scanEnums();
    }

    public void scanEnums() {
        String packageName = "com.alphaess.integrated_energy_platform.pricing"; // 替换成你要扫描的包路径
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(Enum.class));
        scanner.findCandidateComponents(packageName).forEach(def -> {
            try {
                Class<?> enumClass = Class.forName(def.getBeanClassName());
                if (enumClass.isEnum() && DictionaryCode.class.isAssignableFrom(enumClass)) {
                    this.simpleModule.addDeserializer(enumClass, (JsonDeserializer) CodeToEnumConverterFactory.
                            INSTANCE.getConverter((Class<? extends Enum>) enumClass));
                }
            } catch (ClassNotFoundException e) {
                // to do nothing
            }
        });
        this.simpleModule.addSerializer(EnumToStringConverter.INSTANCE);
        this.objectMapper.registerModule(this.simpleModule);
    }

}
