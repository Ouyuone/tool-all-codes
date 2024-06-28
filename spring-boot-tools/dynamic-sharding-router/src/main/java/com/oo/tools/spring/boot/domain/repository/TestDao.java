package com.oo.tools.spring.boot.domain.repository;

import com.oo.tools.spring.boot.domain.model.entity.TestEntity;
import com.oo.tools.spring.boot.supports.annotation.DynamicSwitchRouter;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2024/06/28 14:57:34
 */
@Mapper
@DynamicSwitchRouter
public interface TestDao {

    @DynamicSwitchRouter
    List<TestEntity> queryList();

    @DynamicSwitchRouter(dynamicDB = "slave")
    List<TestEntity> querySlaveList();

    @DynamicSwitchRouter(splitTable = true)
    List<TestEntity> querySharingList(String uId);
}
