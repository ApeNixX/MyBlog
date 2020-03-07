package com.apenixx.blog.service.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * @Author ApeNixX
 * @Date 2020/2/3 20:58
 * @Version 1.0
 * @Describe
 */
public class CustomerUserDetails extends User {
    private  String avatarImgUrl;
    public CustomerUserDetails(String avatarImgUrl,String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.avatarImgUrl=avatarImgUrl;
    }

    public String getAvatarImgUrl() {
        return avatarImgUrl;
    }

    public void setAvatarImgUrl(String avatarImgUrl) {
        this.avatarImgUrl = avatarImgUrl;
    }
}
