package com.apenixx.blog.model;

import lombok.Data;

/**
 * @Author ApeNixX
 * @Date 2020/2/4 13:43
 * @Version 1.0
 * @Describe 文章评论
 */
@Data
public class Comment {
    private long id;

    /**
     * 留言的文章id
     */
    private long articleId;

    /**
     * 回复的父id 若是评论则为 0，则是评论中的回复则为对应评论的id
     */
    private long pId=0;

    /**
     * 评论者
     */
    private int answererId;

    /**
     * 被回复者
     */
    private int respondentId;

    /**
     * 评论日期
     */
    private String commentDate;

    /**
     * 喜欢数
     */
    private int likes=0;

    /**
     * 评论内容
     */
    private String commentContent;

    /**
     * 该条评论是否已读  1--未读   0--已读
     */
    private int isRead = 1;
    /**
     * 定位
     */
    private String location;
}
