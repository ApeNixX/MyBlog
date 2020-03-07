package com.apenixx.blog.service.impl;

import com.apenixx.blog.mapper.TagMapper;
import com.apenixx.blog.model.Tag;
import com.apenixx.blog.service.TagService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author ApeNixX
 * @Date 2020/1/29 17:13
 * @Version 1.0
 * @Describe
 */
@Service
public class TagServiceImpl implements TagService {
    @Autowired
    private TagMapper tagMapper;
    @Override
    public void addTags(String[] tags, int tagSize) {
        for(String tag:tags){
            if(tagMapper.findIsExistByTagName(tag)==0){
                tagMapper.save(new Tag(tag,tagSize));
            }
        }
    }

    @Override
    public List<Tag> findTagsCloud() {
        return tagMapper.findTagsCloud();
    }

    @Override
    public int countTagsNum() {
        return tagMapper.countTagsNum();
    }

    @Override
    public List<Tag> getAllTags(int pageNum, int rows) {
        PageHelper.startPage(pageNum,rows);
        return tagMapper.findTagsCloud();
    }
}
