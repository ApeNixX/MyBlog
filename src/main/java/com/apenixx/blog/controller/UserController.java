package com.apenixx.blog.controller;

import com.apenixx.blog.aspect.annotation.PermissionCheck;
import com.apenixx.blog.model.FriendLink;
import com.apenixx.blog.model.User;
import com.apenixx.blog.redis.StringRedisServiceImpl;
import com.apenixx.blog.service.*;
import com.apenixx.blog.service.impl.UserServiceImpl;
import com.apenixx.blog.utils.BlogJSONResult;
import com.apenixx.blog.utils.FileUtil;
import com.apenixx.blog.utils.MD5Util;
import com.apenixx.blog.utils.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.security.Principal;
import java.util.List;

/**
 * @Author ApeNixX
 * @Date 2020/1/19 16:08
 * @Version 1.0
 * @Describe user控制层
 */
@Controller
@RequestMapping(value = "/user")
@Slf4j
public class UserController {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    CommentService commentService;
    @Autowired
    LeaveMessageService leaveMessageService;
    @Autowired
    RedisService redisService;
    @Autowired
    StringRedisServiceImpl stringRedisService;
    @Autowired
    FriendLinkService friendLinkService;
    @Autowired
    ArticleLikesRecordService articleLikesRecordService;
    @GetMapping(value = "/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/about")
    public String about(HttpServletRequest request) {
        List<FriendLink> data = friendLinkService.getAllFriendLink();
        request.setAttribute("links",data);
        return "about";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/article")
    public String article() {
        return "article";
    }

    @GetMapping("/detail")
    public String detail() {
        return "detail";
    }

    @GetMapping("/archives")
    public String archives() {
        return "archives";
    }

    @GetMapping("/timeline")
    public String timeline() {
        return "timeline";
    }

    @GetMapping("/user")
    public String user() {
        return "user";
    }

    @GetMapping("/resource")
    public String resource() {
        return "resource";
    }




    @GetMapping(value = "/all")
    @ResponseBody
    public List<User> getUser() {
        return userService.findAllUser();
    }


    /**
     * 获得个人资料
     */
    @PostMapping(value = "/getUserPersonalInfo")
    @ResponseBody
    public BlogJSONResult getUserPersonalInfo(@AuthenticationPrincipal Principal principal) {
        String username = principal.getName();
        BlogJSONResult data = userService.getUserPersonalInfoByUsername(username);
        return data;
    }

    /**
     * 保存个人资料
     */
    @PostMapping(value = "/savePersonalDate")
    @ResponseBody
    public BlogJSONResult savePersonalDate(User user, @AuthenticationPrincipal Principal principal){

        String username = principal.getName();
        return userService.savePersonalDate(user, username);

    }

    /**
     * 上传头像
     */
    @PostMapping(value = "/uploadHead")
    @ResponseBody
    public BlogJSONResult uploadHead(HttpServletRequest request,
                             @AuthenticationPrincipal Principal principal) {
        String username = principal.getName();
        String img = request.getParameter("img");
        //获得上传文件的后缀名
        int index = img.indexOf(";base64,");
        String strFileExtendName = "." + img.substring(11,index);
        img = img.substring(index + 8);
        try {
            FileUtil fileUtil = new FileUtil();
            //文件路径名
           // String filePath = this.getClass().getResource("/").getPath().substring(6) + "userImg/";

            String filePath = this.getClass().getResource("/").getPath().substring(1) + "userImg/";
            //文件名时间戳
            TimeUtil timeUtil = new TimeUtil();
            //文件base64转File类型(路径，后缀，文件名)
            File file = fileUtil.base64ToFile(filePath, img, timeUtil.getLongTime() + strFileExtendName);
            String url = fileUtil.uploadFile(file, "user/avatar/" + username);
            int userId = userService.findIdByUsername(username);
            userService.updateAvatarImgUrlById(url, userId);
            BlogJSONResult data = userService.getHeadPortraitUrl(userId);
            return data;
        } catch (Exception e){
            e.printStackTrace();
            log.error("更改头像失败",e.getMessage(),e);
            return BlogJSONResult.errorMsg("更改头像失败");
        }
    }

    /**
     * 获得该用户曾今的所有评论
     */
    @PostMapping(value = "/getUserComment")
    @ResponseBody
    public BlogJSONResult getUserComment(@RequestParam("rows") String rows,
                                 @RequestParam("pageNum") String pageNum,
                                 @AuthenticationPrincipal Principal principal){
        String username = principal.getName();
        BlogJSONResult data = commentService.getUserComment(Integer.parseInt(rows), Integer.parseInt(pageNum), username);
        return data;
    }

    /**
     * 获得该用户曾今的所有留言
     */
    @PostMapping(value = "/getUserLeaveWord")
    @ResponseBody
    public BlogJSONResult getUserLeaveMessage(@RequestParam("rows") String rows,
                                      @RequestParam("pageNum") String pageNum,
                                      @AuthenticationPrincipal Principal principal){
        String username = principal.getName();
        BlogJSONResult data = leaveMessageService.getUserLeaveMessage(Integer.parseInt(rows), Integer.parseInt(pageNum), username);
        return data;
    }

    /**
     * 已读一条消息
     * @param id 消息的id
     * @param msgType 消息是评论消息还是留言消息  1-评论  2--留言
     */
    @GetMapping(value = "/readThisMsg")
    @ResponseBody
    public BlogJSONResult readThisMsg(@RequestParam("id") int id,
                              @RequestParam("msgType") int msgType,
                              @AuthenticationPrincipal Principal principal){
        redisService.readOneMsgOnRedis(userService.findIdByUsername(principal.getName()), msgType);
        if(msgType == 1){
            return commentService.readOneCommentRecord(id);
        } else {
            return leaveMessageService.readOneLeaveMessageRecord(id);
        }
    }

    /**
     * 已读所有消息
     * @param msgType 消息是评论消息还是留言消息  1-评论  2--留言
     */
    @GetMapping(value = "/readAllMsg")
    @ResponseBody
    public BlogJSONResult readAllMsg(@RequestParam("msgType") int msgType,
                             @AuthenticationPrincipal Principal principal){
        String username = principal.getName();
        redisService.readAllMsgOnRedis(userService.findIdByUsername(username), msgType);
        if(msgType == 1){
            return  commentService.readAllComment(username);
        } else {
            return  leaveMessageService.readAllLeaveMessage(username);
        }
    }

    /**
     * 获得用户未读消息
     */
    @PostMapping(value = "/getUserNews")
    @PermissionCheck(value = "ROLE_USER")
    @ResponseBody
    public BlogJSONResult getUserNews(@AuthenticationPrincipal Principal principal){
        String username = principal.getName();
        BlogJSONResult data = redisService.getUserNews(username);
        return data;
    }
    /**
     * 改密码
     */
    @PostMapping(value = "/changePassword")
    @ResponseBody
    public BlogJSONResult changePassword(@RequestParam("phone") String phone,
                                 @RequestParam("authCode") String authCode,
                                 @RequestParam("newPassword") String newPassword){

        String trueMsgCode = (String) stringRedisService.get(phone);

        //判断手机号是否正确
        if (trueMsgCode == null) {
            return BlogJSONResult.errorMsg("手机号错误");
        }
        //判断用户名是否正确
        User user = userService.findUserByPhone(phone);

        if (user == null) {
            return BlogJSONResult.build(55, "该用户不存在", null);
        }
        //判断验证码是否正确
        if (!authCode.equals(trueMsgCode)) {
            return BlogJSONResult.build(44, "验证码错误", null);
        }
     //注册时对密码进行MD5加密
        MD5Util md5Util = new MD5Util();
        String mD5Password = md5Util.encode(newPassword);
        userService.updatePasswordByPhone(phone, mD5Password);
       //修改密码成功删除redis中的验证码
        stringRedisService.remove(phone);

        return BlogJSONResult.ok();
}

    /**
     * 获得文章点赞信息
     */
    @PostMapping(value = "/getArticleThumbsUp", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    @PermissionCheck(value = "ROLE_SUPERADMIN")
    public BlogJSONResult getArticleThumbsUp(@RequestParam("rows") int rows,
                                     @RequestParam("pageNum") int pageNum){
        BlogJSONResult data = articleLikesRecordService.getArticleThumbsUp(rows, pageNum);
        return data;
    }

    /**
     * 已读一条点赞信息
     */
    @GetMapping(value = "/readThisThumbsUp", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
//    @PermissionCheck(value = "ROLE_SUPERADMIN")
    public BlogJSONResult readThisThumbsUp(@RequestParam("id") int id){
        BlogJSONResult data = articleLikesRecordService.readThisThumbsUp(id);
        return data;
    }

    /**
     * 已读所有点赞信息
     */
    @GetMapping(value = "/readAllThumbsUp", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
//    @PermissionCheck(value = "ROLE_SUPERADMIN")
    public BlogJSONResult readAllThumbsUp(){
        BlogJSONResult data = articleLikesRecordService.readAllThumbsUp();
        return data;
    }
}