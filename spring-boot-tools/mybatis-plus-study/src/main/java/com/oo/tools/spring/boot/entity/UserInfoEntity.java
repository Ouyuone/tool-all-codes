package com.oo.tools.spring.boot.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.oo.tools.spring.boot.enums.SexEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * UserInfoEntity</p>
 *
 * @author Yu.ou
 * @desc:
 * @since: 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "user_info", autoResultMap = true)
public class UserInfoEntity extends AbsEntity {
    
    private String name;
    
    private String mobile;
    
    private SexEnum sex;
    
}
