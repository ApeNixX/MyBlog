package com.apenixx.blog.service.impl;

import com.apenixx.blog.component.JavaScriptCheck;
import com.apenixx.blog.constant.SiteOwner;
import com.apenixx.blog.mapper.LeaveMessageMapper;
import com.apenixx.blog.mapper.UserMapper;
import com.apenixx.blog.model.LeaveMessage;
import com.apenixx.blog.model.UserReadNews;
import com.apenixx.blog.redis.HashRedisServiceImpl;
import com.apenixx.blog.service.LeaveMessageService;
import com.apenixx.blog.service.UserService;
import com.apenixx.blog.utils.BlogJSONResult;
import com.apenixx.blog.utils.StringUtil;
import com.apenixx.blog.utils.TimeUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author ApeNixX
 * @Date 2020/2/5 13:27
 * @Version 1.0
 * @Describe
 */
@Service
public class LeaveMessageServiceImpl implements LeaveMessageService {

    @Autowired
    LeaveMessageMapper leaveMessageMapper;
    @Autowired
    UserService userService;
    @Autowired
    HashRedisServiceImpl hashRedisServiceImpl;
    @Autowired
    UserMapper userMapper;

    @Override
    public void publishLeaveMessage(String leaveMessageContent, String pageName, String answerer,String location) {

        TimeUtil timeUtil = new TimeUtil();
        String nowStr = timeUtil.getFormatDateForFive();
        leaveMessageContent = JavaScriptCheck.javaScriptCheck(leaveMessageContent);
        LeaveMessage leaveMessage = new LeaveMessage(pageName, userService.findIdByUsername(answerer), userService.findIdByUsername(SiteOwner.SITE_OWNER), nowStr, leaveMessageContent);
        if(leaveMessage.getAnswererId() == leaveMessage.getRespondentId()){
            leaveMessage.setIsRead(0);
        }
        leaveMessage.setLocation(location);
        leaveMessageMapper.save(leaveMessage);
        //redis中保存该用户未读消息
        addNotReadNews(leaveMessage);
    }

    @Override
    public void publishLeaveMessageReply(LeaveMessage leaveMessage, String respondent) {
        TimeUtil timeUtil = new TimeUtil();
        String nowStr = timeUtil.getFormatDateForFive();
        leaveMessage.setLeaveMessageDate(nowStr);
        leaveMessage.setRespondentId(userService.findIdByUsername(respondent));
        if(leaveMessage.getAnswererId() == leaveMessage.getRespondentId()){
            leaveMessage.setIsRead(0);
        }
        leaveMessageMapper.save(leaveMessage);
        //redis中保存该用户未读消息
        addNotReadNews(leaveMessage);
    }

    @Override
    public BlogJSONResult findAllLeaveMessage(String pageName, int pId, String username) {
        List<LeaveMessage> leaveMessages = leaveMessageMapper.findAllLeaveMessage(pageName, pId);
        JSONObject leaveMessageJson;
        JSONObject replyJson;
        JSONArray replyJsonArray;
        JSONArray leaveMessageJsonArray = new JSONArray();

        List<LeaveMessage> leaveMessageReplies;

        for(LeaveMessage leaveMessage:leaveMessages){
            leaveMessageJson = new JSONObject();
            leaveMessageJson.put("id",leaveMessage.getId());
            leaveMessageJson.put("answerer",userService.findUsernameById(leaveMessage.getAnswererId()));
            leaveMessageJson.put("leaveMessageDate",leaveMessage.getLeaveMessageDate());
            leaveMessageJson.put("likes",leaveMessage.getLikes());
            leaveMessageJson.put("avatarImgUrl",userService.getHeadPortraitUrlByUserId(leaveMessage.getAnswererId()));
            leaveMessageJson.put("leaveMessageContent",leaveMessage.getLeaveMessageContent());
            leaveMessageJson.put("location",leaveMessage.getLocation());
            if (userMapper.getRoleNumByUserId(leaveMessage.getAnswererId())>2){
                leaveMessageJson.put("master",1);
            }else {
                leaveMessageJson.put("master",0);
            }
            leaveMessageReplies = leaveMessageMapper.findLeaveMessageReplyByPageNameAndPid(pageName, leaveMessage.getId());
            replyJsonArray = new JSONArray();
            for(LeaveMessage reply:leaveMessageReplies){
                    replyJson = new JSONObject();
                    replyJson.put("id",reply.getId());
                    replyJson.put("answerer", userService.findUsernameById(reply.getAnswererId()));
                    replyJson.put("respondent", userService.findUsernameById(reply.getRespondentId()));
                    replyJson.put("leaveMessageDate", reply.getLeaveMessageDate());
                    replyJson.put("leaveMessageContent", reply.getLeaveMessageContent());
                    replyJson.put("location",reply.getLocation());
                    replyJson.put("avatarImgUrl",userService.getHeadPortraitUrlByUserId(reply.getAnswererId()));
                if (userMapper.getRoleNumByUserId(reply.getAnswererId())>2){
                    replyJson.put("master",1);
                }else {
                    replyJson.put("master",0);
                }
                    replyJsonArray.add(replyJson);
            }
            leaveMessageJson.put("replies",replyJsonArray);
            leaveMessageJsonArray.add(leaveMessageJson);

        }


        return BlogJSONResult.ok(leaveMessageJsonArray);
    }

    @Override
    public BlogJSONResult getUserLeaveMessage(int rows, int pageNum, String username) {
        int respondentId = userService.findIdByUsername(username);
        PageHelper.startPage(pageNum, rows);
        List<LeaveMessage> leaveMessages = leaveMessageMapper.getUserLeaveMessage(respondentId);
        PageInfo<LeaveMessage> pageInfo = new PageInfo<>(leaveMessages);
        JSONObject returnJson = new JSONObject();
        JSONObject leaveMessageJson;
        JSONArray leaveMessageJsonArray = new JSONArray();
        for(LeaveMessage leaveMessage : leaveMessages){
            leaveMessageJson = new JSONObject();
            leaveMessageJson.put("id",leaveMessage.getId());
            leaveMessageJson.put("pId",leaveMessage.getPId());
            leaveMessageJson.put("pageName",leaveMessage.getPageName());
            leaveMessageJson.put("answerer",userService.findUsernameById(leaveMessage.getAnswererId()));
            leaveMessageJson.put("leaveMessageDate",leaveMessage.getLeaveMessageDate());
            leaveMessageJson.put("isRead",leaveMessage.getIsRead());
            leaveMessageJsonArray.add(leaveMessageJson);
        }

        returnJson.put("result",leaveMessageJsonArray);
        returnJson.put("msgIsNotReadNum",leaveMessageMapper.countIsReadNumByRespondentId(respondentId));

        JSONObject pageJson = new JSONObject();
        pageJson.put("pageNum",pageInfo.getPageNum());
        pageJson.put("pageSize",pageInfo.getPageSize());
        pageJson.put("total",pageInfo.getTotal());
        pageJson.put("pages",pageInfo.getPages());
        pageJson.put("isFirstPage",pageInfo.isIsFirstPage());
        pageJson.put("isLastPage",pageInfo.isIsLastPage());

        returnJson.put("pageInfo",pageJson);
        return BlogJSONResult.ok(returnJson);
    }

    @Override
    public BlogJSONResult readOneLeaveMessageRecord(int id) {
        try {
            leaveMessageMapper.readOneLeaveMessageRecord(id);
            return BlogJSONResult.ok();
        } catch (Exception e){
            e.printStackTrace();
            return BlogJSONResult.errorMsg("已读留言失败");
        }
    }

    @Override
    public BlogJSONResult readAllLeaveMessage(String username) {
        int respondentId = userService.findIdByUsername(username);
        leaveMessageMapper.readLeaveMessageRecordByRespondentId(respondentId);
        return BlogJSONResult.ok();
    }

    @Override
    public List<LeaveMessage> getAllMsg(int pageNum, int rows) {
        PageHelper.startPage(pageNum,rows);
        return leaveMessageMapper.getAllMsg();
    }


    /**
     * 保存评论成功后往redis中增加一条未读评论数
     */
    private void addNotReadNews(LeaveMessage leaveMessage){
        if(leaveMessage.getRespondentId() != leaveMessage.getAnswererId()){
            Boolean isExistKey = hashRedisServiceImpl.hasKey(leaveMessage.getRespondentId()+ StringUtil.BLANK);
            if(!isExistKey){
                UserReadNews news = new UserReadNews(1,0,1);
                hashRedisServiceImpl.put(String.valueOf(leaveMessage.getRespondentId()), news, UserReadNews.class);
            } else {
                hashRedisServiceImpl.hashIncrement(leaveMessage.getRespondentId()+ StringUtil.BLANK, "allNewsNum",1);
                hashRedisServiceImpl.hashIncrement(leaveMessage.getRespondentId()+ StringUtil.BLANK, "leaveMessageNum",1);
            }
        }
    }
}
