package com.apenixx.blog.controller;

import com.apenixx.blog.model.FriendLink;
import com.apenixx.blog.service.FriendLinkService;
import com.apenixx.blog.utils.BlogJSONResult;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author ApeNixX
 * @Date 2020/2/7 22:54
 * @Version 1.0
 * @Describe 友链页面
 */
@RestController
public class FriendLinkController {

    @Autowired
    FriendLinkService friendLinkService;

    /**
     * 获得所有友链信息
     */
    @RequestMapping(value = "/getFriendLinkInfo")
    public Map<String,Object> getFriendLink(int page,int limit){
        List<FriendLink> data = friendLinkService.getFriendLinkByPage(page, limit);
        PageInfo<FriendLink> friendLinkPageInfo = new PageInfo<>(data);
        Map<String,Object> map = new HashMap<>();
        map.put("code",0);
        map.put("data",friendLinkPageInfo.getList());
        map.put("count",friendLinkPageInfo.getTotal());
        return map;
    }


    @PostMapping("/publishFriendLink")
    public BlogJSONResult publishFriendLink(FriendLink friendLink){
        return friendLinkService.addFriendLink(friendLink);
    }

    @PostMapping("/updateFriendLink")
    public BlogJSONResult updateFriendLink(FriendLink friendLink,int id){
        return friendLinkService.updateFriendLink(friendLink,id);
    }

    @PostMapping("/deleteFriendLink")
    public BlogJSONResult deleteFriendLink(int id){
        return friendLinkService.deleteFriendLink(id);
    }
}
