package com.apenixx.blog.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author ApeNixX
 * @Date 2020/2/7 21:47
 * @Version 1.0
 * @Describe
 */
@Data
@NoArgsConstructor
public class FriendLink {
    private int id;

    /**
     * 博主
     */
    private String blogger;

    /**
     * 博主url
     */
    private String url;

    /**
     * 博主头像
     */
    private String img;

    public FriendLink(String blogger, String url,String img){
        this.blogger = blogger;
        this.url = url;
        this.img = img;
    }
}
