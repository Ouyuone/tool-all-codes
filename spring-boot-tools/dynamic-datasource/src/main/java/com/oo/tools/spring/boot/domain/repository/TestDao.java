package com.oo.tools.spring.boot.domain.repository;

import com.oo.tools.spring.boot.domain.model.entity.TestEntity;
import com.oo.tools.spring.boot.types.DynamicSwitch;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2024/06/27 13:45:17
 */
@Mapper
@DynamicSwitch
public interface TestDao {

    @DynamicSwitch("slave")
    List<TestEntity> querySlaveList();


    @DynamicSwitch
    List<TestEntity> queryList();
}
