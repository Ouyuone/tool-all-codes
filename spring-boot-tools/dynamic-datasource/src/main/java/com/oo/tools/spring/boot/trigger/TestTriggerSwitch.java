package com.oo.tools.spring.boot.trigger;

import com.oo.tools.spring.boot.domain.repository.TestDao;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 测试数据源切换
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2024/06/27 13:54:08
 */
@Component
public class TestTriggerSwitch implements CommandLineRunner {


    @Resource
    private TestDao testDao;

    @Override
    public void run(String... args) {
        testDao.queryList();

        testDao.querySlaveList();
        System.out.println("ok");
    }
}
