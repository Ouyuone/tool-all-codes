package com.oo.tools.spring.boot.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.oo.tools.spring.boot.entity.UserInfoEntity;
import com.oo.tools.spring.boot.model.UserPageRes;

import java.util.List;

/**
 * UserInfoRepository</p>
 *
 * @author Yu.ou
 * @desc:
 * @since: 1.0.0
 */

public interface UserInfoRepository extends BaseMapper<UserInfoEntity> {
    
    /**
     * 分页查询用户信息
     * 注意：使用 PageHelper 时，返回类型应该是 List，而不是 Page
     * PageHelper 会自动将查询结果包装在 Page 对象中
     * 分页参数通过 PageHelper.startPage() 方法设置，不需要在 SQL 中使用参数
     *
     * @return 用户信息列表
     */
    List<UserInfoEntity> findPageQuery();
    
    /**
     * 分页查询用户信息（通过方法参数传递分页信息）
     * 使用 PageHelper 的 support-methods-arguments 功能，自动从方法参数中识别分页信息
     * 注意：参数必须实现 PageParam 接口（UserPageRes 继承了 PageRequest，PageRequest 继承了 PageParam）
     * 不需要显式调用 PageHelper.startPage()，PageHelper 会自动识别参数中的分页信息
     *
     * @param pageRequest 分页请求参数，包含 pageNum 和 pageSize
     * @return 用户信息列表（PageHelper 会自动将结果包装在 Page 对象中）
     */
    List<UserInfoEntity> findPageQueryByParam(UserPageRes pageRequest);
    
    /**
     * 分页查询用户信息（返回 Page 类型）
     * 使用 PageHelper 的 support-methods-arguments 功能，自动从方法参数中识别分页信息
     * 注意：参数必须实现 PageParam 接口（UserPageRes 继承了 PageRequest，PageRequest 继承了 PageParam）
     * 不需要显式调用 PageHelper.startPage()，PageHelper 会自动识别参数中的分页信息
     * PageHelper 返回的 List 实际上就是 Page 类型，可以直接返回 Page
     *
     * @param pageRequest 分页请求参数，包含 pageNum 和 pageSize
     * @return Page 对象，包含分页信息和数据列表
     */
    Page<UserInfoEntity> findPageQueryReturnPage(UserPageRes pageRequest);
}
