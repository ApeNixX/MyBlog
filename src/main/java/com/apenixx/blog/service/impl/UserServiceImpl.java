package com.apenixx.blog.service.impl;

import com.apenixx.blog.constant.RoleConstant;
import com.apenixx.blog.mapper.UserMapper;
import com.apenixx.blog.model.User;
import com.apenixx.blog.service.UserService;
import com.apenixx.blog.utils.BlogJSONResult;
import com.apenixx.blog.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author ApeNixX
 * @Date 2020/1/19 15:46
 * @Version 1.0
 * @Describe user业务实现
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;

    @Override
    public List<User> findAllUser() {
        return userMapper.getAllUser();
    }

    @Override
    public boolean usernameIsExist(String username) {
        User user = userMapper.findUsernameByUsername(username);
        return user != null;
    }

    @Override
    public boolean phoneIsExist(String phone) {
        User user = userMapper.findUserByPhone(phone);
        return user!=null;
    }

    @Override
    public BlogJSONResult insert(User user) {
        user.setUsername(user.getUsername().trim().replaceAll(" ", StringUtil.BLANK));
        user.setAvatarImgUrl("https://apenixx-oss.oss-cn-shenzhen.aliyuncs.com/public/user/avatar/张海洋/1579182017.png");
        user.setGender("male");
        userMapper.save(user);
        int userId = userMapper.findUserIdByPhone(user.getPhone());
        insertRole(userId, RoleConstant.ROLE_USER);
        return BlogJSONResult.ok();
    }

    @Override
    public BlogJSONResult getUserPersonalInfoByUsername(String username) {
        User user = userMapper.getUserPersonalInfoByUsername(username);
        return BlogJSONResult.ok(user);
    }

    @Override
    public BlogJSONResult savePersonalDate(User user, String username) {
        user.setUsername(user.getUsername().trim().replaceAll(" ", StringUtil.BLANK));
        String newName = user.getUsername();
        if(newName.length() > StringUtil.USERNAME_MAX_LENGTH) {
            return BlogJSONResult.build(1,"用户名太长",null);
        }else if (StringUtil.BLANK.equals(newName)){
            return BlogJSONResult.build(2,"用户名不能为空",null);
        }

        int status;
        //改了昵称
        if(!newName.equals(username)){
            if(usernameIsExist(newName)){
                return BlogJSONResult.build(3,"用户名已存在",null);
            }
            status = 503;
            //注销当前登录用户
            SecurityContextHolder.getContext().setAuthentication(null);
        }
        //没改昵称
        else {
            status = 504;
        }
        userMapper.savePersonalDate(user, username);
        return BlogJSONResult.build(status,"",null);
    }

    @Override
    public void updateAvatarImgUrlById(String avatarImgUrl, int id) {
        userMapper.updateAvatarImgUrlById(avatarImgUrl, id);
    }

    @Override
    public int findIdByUsername(String username) {
        return userMapper.findIdByUsername(username);
    }

    @Override
    public BlogJSONResult getHeadPortraitUrl(int id) {
        String avatarImgUrl = userMapper.getHeadPortraitUrl(id);
        return BlogJSONResult.ok(avatarImgUrl);
    }

    @Override
    public String findUsernameById(int id) {
        return userMapper.findUsernameById(id);
    }

    @Override
    public String getHeadPortraitUrlByUserId(int userId) {
        return userMapper.getHeadPortraitUrl(userId);
    }

    @Override
    public void updatePasswordByPhone(String phone, String password) {
        userMapper.updatePassword(phone,password);
        //        密码修改成功后注销当前用户
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Override
    public User findUserByPhone(String phone) {
        return userMapper.findUserByPhone(phone);
    }

    @Override
    public void updateRecentlyLanded(String username, String recentlyLanded) {
        String phone = userMapper.findPhoneByUsername(username);
        userMapper.updateRecentlyLanded(phone, recentlyLanded);
    }

    @Override
    public int countUserNum() {
        return userMapper.countUserNum();
    }

    @Override
    public List<User> getAllUser(int pageNum, int rows) {
        PageHelper.startPage(pageNum,rows);
        return userMapper.getAllUser();
    }

    @Override
    public BlogJSONResult updateUserStatus(int status, int id) {
        userMapper.updateUserStatus(status,id);
        return BlogJSONResult.ok();
    }

    @Override
    public List<User> getAllUserAndRoles(int pageNum, int rows) {
        PageHelper.startPage(pageNum,rows);
        return userMapper.getAllUserAndRoles();
    }

    @Override
    public BlogJSONResult updateUserRoles(int roleId, int id) {
        if(roleId==0){
            insertRole(id,RoleConstant.ROLE_ADMIN);
        }else if(roleId==1){
            insertRole(id,RoleConstant.ROLE_SUPERADMIN);
        }else if(roleId==2){
            userMapper.deleteRole(id,RoleConstant.ROLE_ADMIN);
        }else if(roleId==3){
            userMapper.deleteRole(id,RoleConstant.ROLE_SUPERADMIN);
        }
        return BlogJSONResult.ok();
    }


    /**
     * 增加用户权限
     * @param userId 用户id
     * @param roleId 权限id
     */
    private void insertRole(int userId, int roleId) {
        userMapper.saveRole(userId, roleId);
    }
}
