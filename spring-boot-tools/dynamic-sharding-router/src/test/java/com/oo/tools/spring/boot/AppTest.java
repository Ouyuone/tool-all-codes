package com.oo.tools.spring.boot;

import com.oo.tools.spring.boot.domain.repository.TestDao;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * Unit test for simple App.
 */
@SpringBootTest
public class AppTest {

    @Resource
    private TestDao testDao;

    /**
     * Rigourous Test :-)
     */
    @Test
    public void testApp() {
//        testDao.queryList();
        testDao.querySlaveList();

        testDao.querySharingList("5");

    }
}
