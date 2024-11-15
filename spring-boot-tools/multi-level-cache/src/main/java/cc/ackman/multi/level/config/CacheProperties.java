package cc.ackman.multi.level.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "cache")
public class CacheProperties {

    private final Caffeine caffeine = new Caffeine();
    private final Redis redis = new Redis();

    @Getter
    @Setter
    public static class Caffeine {
        private long expiration = 3600;
        private int maxSize = 10000;
    }

    @Getter
    @Setter
    public static class Redis {
        private long expiration = 7200;

    }
}
