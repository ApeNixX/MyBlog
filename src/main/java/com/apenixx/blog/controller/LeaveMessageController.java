package com.apenixx.blog.controller;

import com.apenixx.blog.aspect.annotation.PermissionCheck;
import com.apenixx.blog.component.JavaScriptCheck;
import com.apenixx.blog.mapper.LeaveMessageMapper;
import com.apenixx.blog.model.Comment;
import com.apenixx.blog.model.LeaveMessage;
import com.apenixx.blog.service.LeaveMessageService;
import com.apenixx.blog.service.UserService;
import com.apenixx.blog.utils.BlogJSONResult;
import com.apenixx.blog.utils.StringUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author ApeNixX
 * @Date 2020/2/5 14:13
 * @Version 1.0
 * @Describe
 */
@RestController
public class LeaveMessageController {
    @Autowired
    LeaveMessageService leaveMessageService;
    @Autowired
    UserService userService;
    @Autowired
    LeaveMessageMapper leaveMessageMapper;
    /**
     * 发表留言
     * @param leaveMessageContent 留言内容
     * @param pageName 留言页
     * @param principal 当前用户
     * @return
     */
    @PostMapping(value = "/publishLeaveMessage")
    @PermissionCheck(value = "ROLE_USER")
    public BlogJSONResult publishLeaveMessage(@RequestParam("leaveMessageContent") String leaveMessageContent,
                                      @RequestParam("pageName") String pageName,
                                      @AuthenticationPrincipal Principal principal,@RequestParam("location")String location){
        String answerer = principal.getName();
        leaveMessageService.publishLeaveMessage(leaveMessageContent,pageName, answerer,location);
        BlogJSONResult data = leaveMessageService.findAllLeaveMessage(pageName, 0, answerer);
        return data;
    }


    /**
     * 获得当前页的留言
     * @param pageName 当前页
     * @return
     */
    @GetMapping(value = "/getPageLeaveMessage")
    public BlogJSONResult getPageLeaveMessage(@RequestParam("pageName") String pageName,
                                      @AuthenticationPrincipal Principal principal){
        String username = null;
        if(principal != null){
            username = principal.getName();
        }
        BlogJSONResult data = leaveMessageService.findAllLeaveMessage(pageName, 0, username);
        return data;
    }

    /**
     * 发布留言中的评论
     * @return
     */
    @PostMapping(value = "/publishLeaveMessageReply")
    @PermissionCheck(value = "ROLE_USER")
    public BlogJSONResult publishLeaveMessageReply(LeaveMessage leaveMessage,@RequestParam("pageName") String pageName,
                                           @RequestParam("parentId") String parentId,
                                           @RequestParam("respondent") String respondent,
                                           @RequestParam("location") String location,
                                           @AuthenticationPrincipal Principal principal) {
        String username = principal.getName();
        leaveMessage.setAnswererId(userService.findIdByUsername(username));
        leaveMessage.setPId(Integer.parseInt(parentId.substring(1)));
        leaveMessage.setLeaveMessageContent(JavaScriptCheck.javaScriptCheck(leaveMessage.getLeaveMessageContent()));
        leaveMessage.setLocation(location);
        String commentContent = leaveMessage.getLeaveMessageContent();
        leaveMessage.setLeaveMessageContent(commentContent.trim());
        if(StringUtil.BLANK.equals(leaveMessage.getLeaveMessageContent())){
            return BlogJSONResult.errorMsg("不能为空");
        }

        leaveMessageService.publishLeaveMessageReply(leaveMessage, respondent);
        BlogJSONResult data = leaveMessageService.findAllLeaveMessage(pageName, 0, username);
        return  data;
    }

    @RequestMapping("/getMsgList")
    public Map<String,Object> getCommentList(int page, int limit) {
        PageInfo<LeaveMessage> leaveMessagePageInfo = new PageInfo<>(leaveMessageService.getAllMsg(page,limit));
        Map<String,Object> map = new HashMap<>();
        map.put("code",0);
        map.put("data",leaveMessagePageInfo.getList());
        map.put("count",leaveMessagePageInfo.getTotal());
        return map;
    }


    @PostMapping("/deleteMsg")
    public BlogJSONResult deleteMsg(int id){
        leaveMessageMapper.deleteMsg(id);
        return BlogJSONResult.ok();
    }
}
