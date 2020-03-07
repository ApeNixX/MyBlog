package com.apenixx.blog.model;

import lombok.Data;

/**
 * @Author ApeNixX
 * @Date 2020/1/30 16:52
 * @Version 1.0
 * @Describe 文章归档
 */
@Data
public class Archive {
    private int id;

    /**
     * 归档日期
     */
    private String archiveName;
}
