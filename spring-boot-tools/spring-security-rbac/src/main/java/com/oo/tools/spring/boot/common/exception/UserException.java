package com.oo.tools.spring.boot.common.exception;


/**
 * 用户信息异常类
 *
 * @author fuhua
 */
public class UserException extends BaseException {
    private static final long serialVersionUID = 1L;
    
    public UserException(String code, Object[] args) {
        super("user", code, args, null);
    }
}
