package com.apenixx.blog.config;

import com.apenixx.blog.handle.LoginFailureHandler;
import com.apenixx.blog.service.security.CustomUserServiceImpl;
import com.apenixx.blog.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @Author ApeNixX
 * @Date 2020/1/19 16:48
 * @Version 1.0
 * @Describe
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private LoginFailureHandler loginFailureHandler;

    @Bean
    UserDetailsService customUserService(){
        return new CustomUserServiceImpl();
    }

    //认证管理器配置
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserService())
                //启动MD5加密
                .passwordEncoder(new PasswordEncoder() {
                    MD5Util md5Util = new MD5Util();
                    @Override
                    public String encode(CharSequence rawPassword) {
                        return md5Util.encode((String) rawPassword);
                    }

                    @Override
                    public boolean matches(CharSequence rawPassword, String encodedPassword) {
                        return encodedPassword.equals(md5Util.encode((String)rawPassword));
                    }
                });
    }

    //核心过滤器配置
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/","/index","/aboutme","/archives","/categories","/friendlylink","/tags","/update")
                .permitAll()
                .antMatchers("/editor","/user/user").hasAnyRole("USER")
                .antMatchers("/ali","/mylove").hasAnyRole("ADMIN")
                .antMatchers("/admin/**","/myheart","/today","/yesterday").hasAnyRole("SUPERADMIN","ADMIN")
                .and()
                .formLogin().loginPage("/user/login").defaultSuccessUrl("/article/index/1").failureHandler(loginFailureHandler)
                .and()
                .headers().frameOptions().sameOrigin()
                .and()
                .logout().logoutUrl("/logout").logoutSuccessUrl("/article/index/1");

        http.csrf().disable();
    }

    /**
     * 放行静态资源
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/static/css/**","/static/js/**","/css/**","/js/**","plug/**");
    }


}
