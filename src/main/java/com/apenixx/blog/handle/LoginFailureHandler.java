package com.apenixx.blog.handle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author ApeNixX
 * @Date 2020/2/9 15:42
 * @Version 1.0
 * @Describe
 */
@Component
@Slf4j
public class LoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException exception) throws IOException, ServletException {
        String message = exception.getMessage();
        if (exception instanceof BadCredentialsException){
            message = "密码错误或用户名不存在";
        }
        if(exception instanceof LockedException){
            message = "帐号冻结，请联系管理员进行解封";
        }

        log.error("登录失败："+ message);
        httpServletRequest.getSession().setAttribute("errorMsg",message);
        httpServletResponse.sendRedirect("/user/login");
    }
}
