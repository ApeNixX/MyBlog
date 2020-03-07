package com.apenixx.blog.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author ApeNixX
 * @Date 2020/2/10 23:59
 * @Version 1.0
 * @Describe 文章点赞记录
 */
@Data
@NoArgsConstructor
public class ArticleLikesRecord {
    private long id;

    /**
     * 文章id
     */
    private long articleId;

    /**
     * 点赞人
     */
    private int likerId;

    /**
     * 点赞时间
     */
    private String likeDate;

    /**
     * 该条点赞是否已读  1--未读   0--已读
     */
    private int isRead = 1;

    public ArticleLikesRecord(long articleId, int likerId, String likeDate) {
        this.articleId = articleId;
        this.likerId = likerId;
        this.likeDate = likeDate;
    }
}
