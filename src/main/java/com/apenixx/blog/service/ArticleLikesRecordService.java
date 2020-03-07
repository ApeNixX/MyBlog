package com.apenixx.blog.service;

import com.apenixx.blog.model.ArticleLikesRecord;
import com.apenixx.blog.utils.BlogJSONResult;

/**
 * @Author ApeNixX
 * @Date 2020/2/11 0:02
 * @Version 1.0
 * @Describe 文章点赞记录业务操作
 */
public interface ArticleLikesRecordService {
    /**
     * 文章是否已经点过赞
     * @param articleId 文章id
     * @param username 点赞人
     * @return true--已经点过赞  false--没有点赞
     */
    boolean isLiked(long articleId, String username);

    /**
     * 保存文章中点赞的记录
     * @param articleLikesRecord
     */
    void insertArticleLikesRecord(ArticleLikesRecord articleLikesRecord);

    /**
     * 通过文章id删除文章点赞记录
     * @param articleId 文章id
     */
    void deleteArticleLikesRecordByArticleId(long articleId);

    /**
     * 获得文章点赞信息
     */
    BlogJSONResult getArticleThumbsUp(int rows, int pageNum);

    /**
     * 已读一条点赞记录
     */
    BlogJSONResult readThisThumbsUp(int id);

    /**
     * 已读所有点赞记录
     */
    BlogJSONResult readAllThumbsUp();
}
