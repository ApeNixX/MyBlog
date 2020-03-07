package com.apenixx.blog.service;

import com.apenixx.blog.model.User;
import com.apenixx.blog.utils.BlogJSONResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author ApeNixX
 * @Date 2020/1/19 15:42
 * @Version 1.0
 * @Describe user业务操作
 */
public interface UserService {

    List<User>  findAllUser();

    /**
     * 判断用户名是否存在
     * @param username 用户名
     * @return true--存在  false--不存在
     */
    boolean usernameIsExist(String username);
    /**
     * 判断手机号是否存在
     * @param phone 用户名
     * @return true--存在  false--不存在
     */
    boolean phoneIsExist(String phone);

    /**
     * 注册用户
     */
    @Transactional
    BlogJSONResult insert(User user);

    /**
     * 获得用户个人信息
     * @return
     */
    BlogJSONResult getUserPersonalInfoByUsername(String username);

    /**
     * 保存用户个人信息
     * @param user 个人信息
     * @param username 当前更改的用户
     * @return
     */
    BlogJSONResult savePersonalDate(User user, String username);

    /**
     * 更改头像
     * @param avatarImgUrl 头像地址
     */
    @Transactional
    void updateAvatarImgUrlById(String avatarImgUrl, int id);

    /**
     * 通过用户名查找id
     * @param username
     * @return
     */
    int findIdByUsername(String username);


    /**
     * 获得头像url
     */
    BlogJSONResult getHeadPortraitUrl(int id);


    /**
     * 通过id查找用户名
     * @param id
     * @return
     */
    String findUsernameById(int id);

    /**
     * 获得用户头像的地址
     * @return 头像的url
     */
    String getHeadPortraitUrlByUserId(int userId);

    /**
     * 通过手机号修改密码
     * @param phone 手机号
     * @param password 密码
     */
    void updatePasswordByPhone(String phone, String password);

    /**
     *  通过手机号查找注册用户
     * @param phone 手机号
     * @return 用户
     */
    User findUserByPhone(String phone);


    /**
     * 更新最近登录时间
     * @param username 用户名
     * @param recentlyLanded 最近登录时间
     */
    void updateRecentlyLanded(String username, String recentlyLanded);

    /**
     * 统计总用户量
     * @return
     */
    int countUserNum();


    List<User> getAllUser(int pageNum,int rows);

    BlogJSONResult updateUserStatus(int status,int id);

    List<User> getAllUserAndRoles(int pageNum,int rows);


    BlogJSONResult updateUserRoles(int roleId,int id);

}
