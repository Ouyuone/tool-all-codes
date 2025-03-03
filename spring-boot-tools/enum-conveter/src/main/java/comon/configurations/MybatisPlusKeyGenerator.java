/*
package comon.configurations;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

*/
/**
 * 全局配置mybatisPlus 新增数据为雪花算法
 *
 * @author <a href="mailto:yu.ou@alpha-ess.com">欧宇</a>
 * 2025/2/19 16:06
 *//*

@Component
public class MybatisPlusKeyGenerator implements IdentifierGenerator, InitializingBean {

    private static Integer mainDataCenterId = 12;
    private static final Integer MAIN_WORKER_ID = RandomUtil.randomInt(6);

    // 自定义 数据中心ID
    @Value("${server.data-center-id:12}")
    private Integer dataCenterId;

    @Override
    public void afterPropertiesSet() {
        MybatisPlusKeyGenerator.mainDataCenterId = this.dataCenterId;
    }

//    @EventListener(EnvironmentChangeEvent.class)
//    public void onRefreshProperties() {
//        this.afterPropertiesSet();
//    }

    @Override
    public Number nextId(Object entity) {
        return SnowFlakeUtil.snowflake.nextId();
    }

    public static Long getNextId() {
        return SnowFlakeUtil.snowflake.nextId();
    }

    private static class SnowFlakeUtil {
        private static final Snowflake snowflake = IdUtil.getSnowflake(MAIN_WORKER_ID, mainDataCenterId);
    }

}*/
