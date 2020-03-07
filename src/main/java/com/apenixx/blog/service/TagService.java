package com.apenixx.blog.service;

import com.apenixx.blog.model.Tag;

import java.util.List;

/**
 * @Author ApeNixX
 * @Date 2020/1/29 17:12
 * @Version 1.0
 * @Describe
 */
public interface TagService {
    /**
     * 加入标签
     * @param tags 一群标签
     * @param tagSize 标签大小
     */
    void addTags(String[] tags, int tagSize);

    /**
     * 获得标签云
     * @return
     */
    List<Tag> findTagsCloud();

    /**
     * 获得标签云数量
     * @return
     */
    int countTagsNum();


    List<Tag> getAllTags(int pageNum,int rows);

}
