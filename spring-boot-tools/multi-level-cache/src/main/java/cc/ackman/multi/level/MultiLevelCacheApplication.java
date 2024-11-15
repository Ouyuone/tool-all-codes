package cc.ackman.multi.level;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * {@link}
 *
 * @author <a href="mailto:it_magicjson@163.com">MagicJson</a>
 * @since 1.0.0
 */

@SpringBootApplication
public class MultiLevelCacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultiLevelCacheApplication.class, args);
    }
}
