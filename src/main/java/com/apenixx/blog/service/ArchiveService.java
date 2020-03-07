package com.apenixx.blog.service;

import com.apenixx.blog.utils.BlogJSONResult;

/**
 * @Author ApeNixX
 * @Date 2020/1/30 16:49
 * @Version 1.0
 * @Describe 归档业务操作
 */
public interface ArchiveService {
    /**
     * 获得归档信息
     * @return
     */
    BlogJSONResult findArchiveNameAndArticleNum();

    /**
     * 添加归档日期
     * @param archiveName
     */
    void addArchiveName(String archiveName);
}
