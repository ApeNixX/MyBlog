package com.apenixx.blog.model;

import lombok.Data;

/**
 * @Author ApeNixX
 * @Date 2020/2/8 15:26
 * @Version 1.0
 * @Describe
 */
@Data
public class Resource {
    private int id;
    private String resourceName;
    private String resourceUserName;
    private String resourcePath;
    private String createTime;
    private String imgSrc;
    private String resourceDescribe;
    private String resourceTypeName;
    private int status=0;

}
