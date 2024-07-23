package com.oo.tools.spring.boot;

/**
 * 用作Http请求时候，自定义验证数据
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2024/03/08 11:33:24
 */
public interface CheckRequest {

    void checkRequest() throws CheckRequestException;
}