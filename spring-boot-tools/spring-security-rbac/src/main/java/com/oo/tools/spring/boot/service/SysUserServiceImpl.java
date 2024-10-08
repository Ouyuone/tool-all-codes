package com.oo.tools.spring.boot.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.oo.tools.spring.boot.common.utils.StringUtils;
import com.oo.tools.spring.boot.entity.SysRole;
import com.oo.tools.spring.boot.entity.SysUser;
import com.oo.tools.spring.boot.mapper.SysRoleMapper;
import com.oo.tools.spring.boot.mapper.SysUserMapper;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


/**
 * 用户 业务层处理
 *
 * @author fuhua
 */
@Service
public class SysUserServiceImpl implements ISysUserService {
    private static final Logger log = LoggerFactory.getLogger(SysUserServiceImpl.class);
    
    @Autowired
    private SysUserMapper userMapper;
    
    
    
    
    
    
    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserByUserName(String userName) {
        return userMapper.selectUserByUserName(userName);
    }
    
   
}
