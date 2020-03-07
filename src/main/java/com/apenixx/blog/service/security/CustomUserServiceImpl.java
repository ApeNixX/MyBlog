package com.apenixx.blog.service.security;

import com.apenixx.blog.mapper.UserMapper;
import com.apenixx.blog.model.Role;
import com.apenixx.blog.model.User;
import com.apenixx.blog.service.UserService;
import com.apenixx.blog.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author ApeNixX
 * @Date 2020/1/19 18:45
 * @Version 1.0
 * @Describe 用户登录处理
 */
public class CustomUserServiceImpl implements UserDetailsService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    UserService userService;
    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
       User user = userMapper.getUsernameAndRolesByPhone(phone);

        if(user == null){
            throw  new UsernameNotFoundException("用户不存在");
        }

        TimeUtil timeUtil = new TimeUtil();
        String recentlyLanded = timeUtil.getFormatDateForSix();
        userService.updateRecentlyLanded(user.getUsername(), recentlyLanded);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for(Role role : user.getRoles()){
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }

        //调用扩展后的CustomerUserDetails，这样就将avatarImgUrl属性扩展并交给springsecurity存放到session了
        if (user.getStatus()==0){
            CustomerUserDetails userdetail = new CustomerUserDetails(user.getAvatarImgUrl(),
                    user.getUsername(), user.getPassword(),authorities);
            return userdetail;
        }else {
            return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),false,false,false,false,authorities);
        }

//        return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),authorities);

    }
}
