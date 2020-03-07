package com.apenixx.blog.model;

import lombok.Data;

/**
 * @Author ApeNixX
 * @Date 2020/1/20 14:12
 * @Version 1.0
 * @Describe
 */

@Data
public class Article {
    private static final long serialVersionUID = 1L;

    private int id;

    /**
     * 文章评论数量
     */
    private int commentNum;
    /**
     * 文章id
     */
    private long articleId;

    /**
     * 文章作者
     */
    private String author;

    /**
     * 文章原作者
     */
    private String originalAuthor;

    /**
     * 文章名
     */
    private String articleTitle;

    /**
     * 发布时间
     */
    private String publishDate;

    /**
     * 最后一次修改时间
     */
    private String updateDate;

    /**
     * 文章内容
     */
    private String articleContent;

    /**
     * 文章标签
     */
    private String articleTags;

    /**
     * 标签数组
     */
    private String[] tagValue;
    /**
     * 文章类型
     */
    private String articleType;

    /**
     * 博客分类
     */
    private String articleCategories;


    /**
     * 原文链接
     * 转载：则是转载的链接
     * 原创：则是在本博客中的链接
     */
    private String articleUrl;

    /**
     * 文章摘要
     */
    private String articleTabloid;

    /**
     * 上一篇文章id
     */
    private long lastArticleId;

    /**
     * 下一篇文章id
     */
    private long nextArticleId;

    /**
     * 喜欢
     */
    private int likes = 0;

/**
 * 文章封面
 */
    private String articleCover;

    /**
     * 文章浏览量
     */
    private long looks = 0;


}
