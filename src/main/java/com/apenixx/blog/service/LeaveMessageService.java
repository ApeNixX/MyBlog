package com.apenixx.blog.service;

import com.apenixx.blog.model.Comment;
import com.apenixx.blog.model.LeaveMessage;
import com.apenixx.blog.utils.BlogJSONResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author ApeNixX
 * @Date 2020/2/5 13:25
 * @Version 1.0
 * @Describe 留言业务操作
 */
public interface LeaveMessageService {
    /**
     * 保存留言信息
     * @param leaveMessageContent 留言内容
     * @param pageName 留言页
     * @param answerer 留言者
     */
    @Transactional
    void publishLeaveMessage(String leaveMessageContent, String pageName, String answerer,String location);


    /**
     * 保存留言回复信息
     * @param leaveMessage
     */
    @Transactional
    void publishLeaveMessageReply(LeaveMessage leaveMessage, String respondent);

    /**
     * 获得当前页的所有留言
     * @param pageName 当前页的名称
     * @param pId
     * @return
     */
    BlogJSONResult findAllLeaveMessage(String pageName, int pId, String username);

    /**
     * 分页获得用户所有留言
     */
    BlogJSONResult getUserLeaveMessage(int rows, int pageNum, String username);

    /**
     * 已读一条留言
     * @param id 评论id
     */
    BlogJSONResult readOneLeaveMessageRecord(int id);

    /**
     * 全部标记为已读
     */
    BlogJSONResult readAllLeaveMessage(String username);


    List<LeaveMessage> getAllMsg(int pageNum, int rows);

}
