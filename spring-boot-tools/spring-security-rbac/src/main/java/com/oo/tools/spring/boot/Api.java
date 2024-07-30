package com.oo.tools.spring.boot;

import com.oo.tools.spring.boot.annotation.Anonymous;
import com.oo.tools.spring.boot.common.Constants;
import com.oo.tools.spring.boot.core.domain.AjaxResult;
import com.oo.tools.spring.boot.core.domain.LoginBody;
import com.oo.tools.spring.boot.service.SysLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2024/07/30 14:42:17
 */
@RestController
@RequestMapping("/")
public class Api {
    
    @Autowired
    private SysLoginService loginService;
    
    @Anonymous
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody) {
        AjaxResult ajax = AjaxResult.success();
        // 生成令牌
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid());
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }
}
