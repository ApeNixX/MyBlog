package com.apenixx.blog.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author ApeNixX
 * @Date 2020/2/4 17:11
 * @Version 1.0
 * @Describe 用户评论留言未读数
 */
@Data
@NoArgsConstructor
public class UserReadNews  implements Serializable {
    /**
     * 留言+评论未读数
     */
    private int allNewsNum;

    /**
     * 评论未读数
     */
    private int commentNum;

    /**
     * 留言未读数
     */
    private int leaveMessageNum;

    public UserReadNews(int allNewsNum, int commentNum, int leaveMessageNum) {
        this.allNewsNum = allNewsNum;
        this.commentNum = commentNum;
        this.leaveMessageNum = leaveMessageNum;
    }
}