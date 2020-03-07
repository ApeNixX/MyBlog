package com.apenixx.blog.service;

import com.apenixx.blog.model.Timeline;
import com.apenixx.blog.utils.BlogJSONResult;

import java.util.List;

/**
 * @Author ApeNixX
 * @Date 2020/2/7 12:58
 * @Version 1.0
 * @Describe
 */
public interface TimeLineService {

    List<Timeline> getAllTimeLine();

    List<Timeline> getAllTimeLineByPage(int pageNum,int rows);


    BlogJSONResult insertTimeLine(Timeline timeline);
}
