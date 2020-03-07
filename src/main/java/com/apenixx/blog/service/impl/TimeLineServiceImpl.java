package com.apenixx.blog.service.impl;

import com.apenixx.blog.mapper.TimelineMapper;
import com.apenixx.blog.model.Timeline;
import com.apenixx.blog.service.TimeLineService;
import com.apenixx.blog.utils.BlogJSONResult;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author ApeNixX
 * @Date 2020/2/7 12:59
 * @Version 1.0
 * @Describe
 */
@Service
public class TimeLineServiceImpl implements TimeLineService {
    @Autowired
    private TimelineMapper timelineMapper;

    @Override
    public List<Timeline> getAllTimeLine() {
        return timelineMapper.getAllTimeLine();
    }

    @Override
    public List<Timeline> getAllTimeLineByPage(int pageNum, int rows) {
        PageHelper.startPage(pageNum,rows);
        return timelineMapper.getAllTimeLine();
    }

    @Override
    public BlogJSONResult insertTimeLine(Timeline timeline) {
        timelineMapper.save(timeline);
        return BlogJSONResult.ok();
    }

}
