package com.apenixx.blog.service;

import com.apenixx.blog.model.Comment;
import com.apenixx.blog.model.VO.CommentVO;
import com.apenixx.blog.utils.BlogJSONResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Author ApeNixX
 * @Date 2020/2/4 13:47
 * @Version 1.0
 * @Describe 评论业务操作
 */
public interface CommentService {
    /**
     * 通过文章id获得文章所有评论和回复
     * @param articleId 文章id
     * @return
     */
    BlogJSONResult findCommentByArticleId(long articleId, String username);

    /**
     * 保存留言
     * @param comment 留言
     */
    @Transactional
    void insertComment(Comment comment);

    /**
     * 返回评论中的回复
     * @param comment
     * @return
     */
    BlogJSONResult replyReplyReturn(Comment comment, String answerer, String respondent);

    /**
     * 分页获得用户所有的评论
     * @param rows 一页大小
     * @param pageNum 当前页
     * @param username 用户
     * @return
     */
    BlogJSONResult getUserComment(int rows, int pageNum, String username);

    /**
     * 已读一条评论
     * @param id 评论id
     */
    BlogJSONResult readOneCommentRecord(int id);

    /**
     * 将该用户的所有未读消息标记为已读
     */
    BlogJSONResult readAllComment(String username);

    List<CommentVO> findFiveNewComment();

    List<Comment> getAllComment(int pageNum,int rows);
}
