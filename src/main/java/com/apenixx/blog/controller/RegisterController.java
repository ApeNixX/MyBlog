package com.apenixx.blog.controller;

import com.apenixx.blog.model.User;
import com.apenixx.blog.redis.StringRedisServiceImpl;
import com.apenixx.blog.service.UserService;
import com.apenixx.blog.utils.BlogJSONResult;
import com.apenixx.blog.utils.MD5Util;
import com.apenixx.blog.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author ApeNixX
 * @Date 2020/2/2 23:06
 * @Version 1.0
 * @Describe
 */
@RestController
public class RegisterController {
    @Autowired
    UserService userService;
    @Autowired
    StringRedisServiceImpl stringRedisService;

    /**
     * 注册用户
     * @param user
     * @return
     */
    @PostMapping("/register")
    public BlogJSONResult register(User user, HttpServletRequest request) {
        String authCode = request.getParameter("authCode");
        String trueMsgCode = (String) stringRedisService.get(user.getPhone());
       //判断手机号是否正确
        if(trueMsgCode == null){
            return BlogJSONResult.errorMsg("手机号错误");
        }
        //判断用户名是否正确
        if(userService.usernameIsExist(user.getUsername())) {
            return BlogJSONResult.build(55, "该用户已存在", null);
        }
        //判断验证码是否正确
        if(!authCode.equals(trueMsgCode)){
            return BlogJSONResult.build(44,"验证码错误",null);
        }
        //注册时对密码进行MD5加密
        MD5Util md5Util = new MD5Util();
        user.setPassword(md5Util.encode(user.getPassword()));
        //注册结果
        BlogJSONResult result = userService.insert(user);

        if(result.getStatus()==200){
            //注册成功删除redis中的验证码
            stringRedisService.remove(user.getPhone());
        }
        return BlogJSONResult.ok();
    }

    /**
     * 用户检测
     * @param username 用户名称
     * @return 不存在 -- ok()     存在 -- errorMsg("该用户已存在")
     */
    @PostMapping("/usernameCheck")
    public BlogJSONResult usernameCheck(String username){
        if(StringUtil.BLANK.equals(username)){
            return BlogJSONResult.build(4,"用户名为空",null);
        }
        else if(userService.usernameIsExist(username)){
            return BlogJSONResult.errorMsg("该用户已存在");
        }else {
            return  BlogJSONResult.ok();
        }
    }

    /**
     * 手机号检测
     * @param phone
     * @return 不存在 -- ok()     存在 -- errorMsg("该手机号已被注册")
     */
    @PostMapping("/phoneCheck")
    public BlogJSONResult phoneCheck(String phone){
        if(userService.phoneIsExist(phone)){
            return BlogJSONResult.errorMsg("该手机号已被注册");
        }else{
            return BlogJSONResult.ok();
        }
    }

}
