package com.apenixx.blog.service;

import com.apenixx.blog.model.FriendLink;
import com.apenixx.blog.utils.BlogJSONResult;

import java.util.List;

/**
 * @Author ApeNixX
 * @Date 2020/2/7 22:44
 * @Version 1.0
 * @Describe
 */
public interface FriendLinkService {
    BlogJSONResult addFriendLink(FriendLink friendLink);

    List<FriendLink> getAllFriendLink();

    BlogJSONResult updateFriendLink(FriendLink friendLink, int id);

    BlogJSONResult deleteFriendLink(int id);

    List<FriendLink> getFriendLinkByPage(int pageNum,int rows);
}
