package com.apenixx.blog.component;

import com.apenixx.blog.mapper.HistoricalViewsMapper;
import com.apenixx.blog.redis.HashRedisServiceImpl;
import com.apenixx.blog.service.ArticleService;
import com.apenixx.blog.service.VisitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

/**
 * @Author ApeNixX
 * @Date 2020/2/11 20:17
 * @Version 1.0
 * @Describe 定时任务
 */
@Component
public class ScheduledTask {
    @Autowired
    HashRedisServiceImpl hashRedisService;
    @Autowired
    VisitorService visitorService;
    @Autowired
    ArticleService articleService;
    @Autowired
    HistoricalViewsMapper historicalViewsMapper;
    /**
     * cron表达式生成器：http://cron.qqe2.com/
     *
     * 每晚24点清空redis中当日网站访问记录，但保存totalVisitor、visitorVolume、yesterdayVisitor
     */
    @Scheduled(cron = "0 0 0 * * ? ")
    public void resetVisitorNumber() {
        long oldTotalVisitor = visitorService.getTotalVisitor();
        long newTotalVisitor = Long.valueOf(hashRedisService.get("visitor", "totalVisitor").toString());
        long yesterdayVisitor = newTotalVisitor - oldTotalVisitor;
        if (hashRedisService.hasHashKey("visitor", "yesterdayVisitor")) {
            hashRedisService.put("visitor", "yesterdayVisitor", yesterdayVisitor);
        } else {
            hashRedisService.put("visitor", "yesterdayVisitor", oldTotalVisitor);
        }
        //写入每日访问量数据库
        historicalViewsMapper.writeHistoricalViews((int)yesterdayVisitor);
        //将redis中的所有访客记录更新到数据库中
        LinkedHashMap map = (LinkedHashMap) hashRedisService.getAllFieldAndValue("visitor");
        String pageName;
        for (Object e : map.keySet()) {
            pageName = String.valueOf(e);
            visitorService.updateVisitorNumByPageName(pageName, String.valueOf(map.get(e)));
            if (pageName.length() > 18) {
                articleService.updateArticleLooks(Long.valueOf(String.valueOf(map.get(e))).longValue(), Long.valueOf(pageName.substring(16)).longValue());
            }
        }
    }
}
