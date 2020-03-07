package com.apenixx.blog.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author ApeNixX
 * @Date 2020/1/29 17:06
 * @Version 1.0
 * @Describe
 */
@Data
@NoArgsConstructor
public class Tag {
    private int id;

    /**
     * 标签名
     */
    private String tagName;

    /**
     * 标签大小
     */
    private int tagSize;

    public Tag(String tagName, int tagSize) {
        this.tagName = tagName;
        this.tagSize = tagSize;
    }
}
