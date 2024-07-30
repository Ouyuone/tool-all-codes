package com.oo.tools.spring.boot.service;

import com.oo.tools.spring.boot.entity.SysUser;

import java.util.List;


/**
 * 用户 业务层
 *
 * @author fuhua
 */
public interface ISysUserService {
  
    
    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    public SysUser selectUserByUserName(String userName);
    
    
}
