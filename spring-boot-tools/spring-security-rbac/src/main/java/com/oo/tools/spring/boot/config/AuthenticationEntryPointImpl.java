package com.oo.tools.spring.boot.config;

import java.io.IOException;
import java.io.Serializable;


import com.oo.tools.spring.boot.common.HttpStatus;
import com.oo.tools.spring.boot.common.utils.ServletUtils;
import com.oo.tools.spring.boot.common.utils.StringUtils;
import com.oo.tools.spring.boot.core.domain.AjaxResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson2.JSON;


/**
 * 认证失败处理类 返回未授权
 *
 * @author fuhua
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint, Serializable {
    private static final long serialVersionUID = -8970718410437077606L;
    
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
            throws IOException {
        int code = HttpStatus.UNAUTHORIZED;
        String msg = StringUtils.format("请求访问：{}，认证失败，无法访问系统资源", request.getRequestURI());
        ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.error(code, msg)));
    }
}
