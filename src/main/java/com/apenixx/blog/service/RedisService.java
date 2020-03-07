package com.apenixx.blog.service;

import com.apenixx.blog.model.UserReadNews;
import com.apenixx.blog.redis.HashRedisServiceImpl;
import com.apenixx.blog.redis.StringRedisServiceImpl;
import com.apenixx.blog.utils.BlogJSONResult;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @Author ApeNixX
 * @Date 2020/2/5 18:52
 * @Version 1.0
 * @Describe redis业务逻辑
 */
@Service
public class RedisService {
    @Autowired
    StringRedisServiceImpl stringRedisServiceImpl;
    @Autowired
    HashRedisServiceImpl hashRedisServiceImpl;
    @Autowired
    UserService userService;

    /**
     * 获得redis中用户的未读消息
     */
    public BlogJSONResult getUserNews(String username) {
        JSONObject jsonObject = new JSONObject();

        int userId = userService.findIdByUsername(username);
        LinkedHashMap map = (LinkedHashMap) hashRedisServiceImpl.getAllFieldAndValue(String.valueOf(userId));
        if(map.size() == 0){
            jsonObject.put("result", 0);
        } else {
            int allNewNum = (int) map.get("allNewsNum");
            int commentNum = (int) map.get("commentNum");
            int leaveMessageNum = (int) map.get("leaveMessageNum");
            UserReadNews news = new UserReadNews(allNewNum, commentNum, leaveMessageNum);
            jsonObject.put("result", news);
        }
        return BlogJSONResult.ok(jsonObject);
    }


    /**
     * 已读一条消息时修改redis中的未读消息
     */
    public void readOneMsgOnRedis(int userId, int msgType) {
        LinkedHashMap map = (LinkedHashMap) hashRedisServiceImpl.getAllFieldAndValue(String.valueOf(userId));
        int allNewsNum = (int) map.get("allNewsNum");
        hashRedisServiceImpl.hashIncrement(String.valueOf(userId), "allNewsNum", -1);
        //如果总留言评论数为0则删除该key
        if(--allNewsNum == 0){
            hashRedisServiceImpl.hashDelete(String.valueOf(userId), UserReadNews.class);
        } else if (msgType == 1){
            hashRedisServiceImpl.hashIncrement(String.valueOf(userId), "commentNum", -1);
        } else {
            hashRedisServiceImpl.hashIncrement(String.valueOf(userId), "leaveMessageNum", -1);
        }
    }

    /**
     * 已读所有消息时修改redis中的未读消息
     */
    public void readAllMsgOnRedis(int userId, int msgType) {
        LinkedHashMap map = (LinkedHashMap) hashRedisServiceImpl.getAllFieldAndValue(String.valueOf(userId));
        int commentNum = (int) map.get("commentNum");
        int leaveMessageNum = (int) map.get("leaveMessageNum");
        if(commentNum == 0 || leaveMessageNum == 0){
            hashRedisServiceImpl.hashDelete(String.valueOf(userId), UserReadNews.class);
        } else if (msgType == 1){
            hashRedisServiceImpl.hashIncrement(String.valueOf(userId), "allNewsNum", -commentNum);
            hashRedisServiceImpl.hashIncrement(String.valueOf(userId), "commentNum", -commentNum);
        } else {
            hashRedisServiceImpl.hashIncrement(String.valueOf(userId), "allNewsNum", -leaveMessageNum);
            hashRedisServiceImpl.hashIncrement(String.valueOf(userId), "leaveMessageNum", -leaveMessageNum);
        }
    }


    /**
     * 修改redis中的点赞未读量
     */
    public void readThumbsUpRecordOnRedis(String key, int increment){
        boolean thumbsUpNotReadIsExist = stringRedisServiceImpl.hasKey(key);
        if(!thumbsUpNotReadIsExist){
            stringRedisServiceImpl.set(key, 1);
        } else {
            stringRedisServiceImpl.stringIncrement(key, increment);
        }
    }

    /**
     * 增加redis中的访客量
     */
    public Long addVisitorNumOnRedis(String key, Object field, long increment){
        boolean fieldIsExist = hashRedisServiceImpl.hasHashKey(key, field);
        if(fieldIsExist){
            return hashRedisServiceImpl.hashIncrement(key, field, increment);
        }
        return null;
    }

    /**
     * 向redis中保存访客量
     */
    public Long putVisitorNumOnRedis(String key, Object field, Object value){
        hashRedisServiceImpl.put(key, field, value);
        return Long.valueOf(hashRedisServiceImpl.get(key, field).toString());
    }

    /**
     * 获得redis中的访客记录
     */
    public Long getVisitorNumOnRedis(String key, Object field){
        boolean fieldIsExist = hashRedisServiceImpl.hasHashKey(key, field);
        if(fieldIsExist){
            return Long.valueOf(hashRedisServiceImpl.get(key, field).toString());
        }
        return null;
    }
}
