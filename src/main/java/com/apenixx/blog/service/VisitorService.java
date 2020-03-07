package com.apenixx.blog.service;

import com.apenixx.blog.utils.BlogJSONResult;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author ApeNixX
 * @Date 2020/2/11 16:55
 * @Version 1.0
 * @Describe 访客业务操作
 */
public interface VisitorService {
    /**
     * 通过页名增加访客量
     * @param pageName
     */
    BlogJSONResult addVisitorNumByPageName(String pageName, HttpServletRequest request);


    /**
     * 通过页名获得访客量
     * @param pageName 页名
     * @return 访客量
     */
    long getNumByPageName(String pageName);

    /**
     * 发布文章后保存该文章的访客量
     * @param pageName 文章url
     */
    void insertVisitorArticlePage(String pageName);

    /**
     * 获得总访问量
     * @return
     */
    long getTotalVisitor();

    /**
     * 通过页名更新访客人数
     */
    void updateVisitorNumByPageName(String pageName, String visitorNum);
}
