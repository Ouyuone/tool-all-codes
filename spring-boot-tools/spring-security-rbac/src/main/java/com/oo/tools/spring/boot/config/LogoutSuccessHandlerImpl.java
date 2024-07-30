package com.oo.tools.spring.boot.config;

import java.io.IOException;

import com.oo.tools.spring.boot.common.threads.AsyncManager;
import com.oo.tools.spring.boot.common.utils.MessageUtils;
import com.oo.tools.spring.boot.common.utils.ServletUtils;
import com.oo.tools.spring.boot.common.utils.StringUtils;
import com.oo.tools.spring.boot.core.domain.AjaxResult;
import com.oo.tools.spring.boot.core.domain.LoginUser;
import com.oo.tools.spring.boot.service.TokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import com.alibaba.fastjson2.JSON;


/**
 * 自定义退出处理类 返回成功
 *
 * @author fuhua
 */
@Configuration
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {
    @Autowired
    private TokenService tokenService;
    
    /**
     * 退出处理
     *
     * @return
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (StringUtils.isNotNull(loginUser)) {
            String userName = loginUser.getUsername();
            // 删除用户缓存记录
            tokenService.delLoginUser(loginUser.getToken());
            // 记录用户退出日志
        }
        ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.success(MessageUtils.message("user.logout.success"))));
    }
}
