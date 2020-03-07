package com.apenixx.blog.service.impl;

import com.apenixx.blog.mapper.FriendLinkMapper;
import com.apenixx.blog.model.FriendLink;
import com.apenixx.blog.service.FriendLinkService;
import com.apenixx.blog.utils.BlogJSONResult;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author ApeNixX
 * @Date 2020/2/7 22:45
 * @Version 1.0
 * @Describe
 */
@Service
@Slf4j
public class FriendLinkServiceImpl  implements FriendLinkService {

    @Autowired
    FriendLinkMapper friendLinkMapper;


    @Override
    public BlogJSONResult addFriendLink(FriendLink friendLink) {
        int id = friendLinkMapper.findIsExistByBlogger(friendLink.getBlogger());
        try {
            if(id == 0){
                friendLinkMapper.save(friendLink);
                return BlogJSONResult.ok();
            } else {
                return BlogJSONResult.errorMsg("友链已存在");
            }
        } catch (Exception e){
            log.error("add friend link exception", e);
            return BlogJSONResult.errorException("添加友链异常");
        }
    }

    @Override
    public List<FriendLink> getAllFriendLink() {
        return friendLinkMapper.getAllFriendLink();
    }

    @Override
    public BlogJSONResult updateFriendLink(FriendLink friendLink, int id) {
        try {
            friendLinkMapper.updateFriendLink(friendLink, id);
            return BlogJSONResult.ok();
        } catch (Exception e){
            log.error("update friend link exception", e);
            return BlogJSONResult.errorException("更新友链异常");
        }
    }

    @Override
    public BlogJSONResult deleteFriendLink(int id) {
        try {
            friendLinkMapper.deleteFriendLinkById(id);
            return BlogJSONResult.ok();
        } catch (Exception e){
            log.error("delete friend link exception", e);
            return BlogJSONResult.errorException("删除友链异常");
        }
    }



    @Override
    public List<FriendLink> getFriendLinkByPage(int pageNum,int rows) {
        PageHelper.startPage(pageNum, rows);
        return friendLinkMapper.getAllFriendLink();
    }
}
