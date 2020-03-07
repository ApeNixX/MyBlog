package com.apenixx.blog.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author: ApeNixX
 * @Date: 2020/1/19 15:30
 * Describe: 用户实体类
 */
@Data
@NoArgsConstructor
public class User {

    private int id;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 性别
     */
    private String gender;

    /**
     * 真实姓名
     */
    private String trueName;

    /**
     * 生日
     */
    private String birthday;

    /**
     * 个人简介
     */
    private String personalBrief;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 最后登录时间
     */
    private String recentlyLanded;

    /**
     * 头像地址
     */
    private String avatarImgUrl;

    /**
     * 是否被冻结 0正常 1冻结
     */
    private int status=0;

    private List<Role> roles;

    public User(String phone, String username, String password, String gender) {
        this.phone = phone;
        this.username = username;
        this.password = password;
        this.gender = gender;
    }

    public User(String phone, String username, String password, String gender, String trueName, String birthday, String personalBrief, String email, String recentlyLanded, String avatarImgUrl) {
        this.phone = phone;
        this.username = username;
        this.password = password;
        this.gender = gender;
        this.trueName = trueName;
        this.birthday = birthday;
        this.personalBrief = personalBrief;
        this.email = email;
        this.recentlyLanded = recentlyLanded;
        this.avatarImgUrl = avatarImgUrl;
    }
}
