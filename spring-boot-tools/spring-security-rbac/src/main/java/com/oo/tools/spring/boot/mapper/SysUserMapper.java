package com.oo.tools.spring.boot.mapper;

import com.oo.tools.spring.boot.entity.SysUser;

/**
 * 用户表 数据层
 *
 * @author fuhua
 */
public interface SysUserMapper {

    
    
    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    public SysUser selectUserByUserName(String userName);
    
   
}
