package com.apenixx.blog.model;

import lombok.Data;

/**
 * @Author ApeNixX
 * @Date 2020/2/11 16:52
 * @Version 1.0
 * @Describe 访客
 */
@Data
public class Visitor {

    private int id;

    /**
     * 访客人数
     */
    private long visitorNum;

    /**
     * 当前页的name or 文章名
     */
    private String pageName;
}
