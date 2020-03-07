package com.apenixx.blog.controller;

import com.apenixx.blog.mapper.TagMapper;
import com.apenixx.blog.model.Tag;
import com.apenixx.blog.model.Timeline;
import com.apenixx.blog.service.TagService;
import com.apenixx.blog.utils.BlogJSONResult;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author ApeNixX
 * @Date 2020/1/29 17:15
 * @Version 1.0
 * @Describe
 */
@RestController
public class TagController {
    @Autowired
    private TagService tagService;
    @Autowired
    private TagMapper tagMapper;
    @GetMapping("/findTagsCloud")
    public BlogJSONResult getAllTags(){
        return BlogJSONResult.ok(tagService.findTagsCloud());
    }


    @RequestMapping("/getTags")
    public Map<String,Object> getTags(int page, int limit) {
        PageInfo<Tag> tagPageInfo = new PageInfo<>(tagService.getAllTags(page,limit));
        Map<String,Object> map = new HashMap<>();
        map.put("code",0);
        map.put("data",tagPageInfo.getList());
        map.put("count",tagPageInfo.getTotal());
        return map;
    }

    @PostMapping("/updateTags")
    public BlogJSONResult updateTags(Tag tag){
        return BlogJSONResult.ok(tagMapper.updateTags(tag));
    }

    @PostMapping("/deleteTags")
    public BlogJSONResult deleteTags(int id){
        tagMapper.deleteTags(id);
        return BlogJSONResult.ok();
    }



}
