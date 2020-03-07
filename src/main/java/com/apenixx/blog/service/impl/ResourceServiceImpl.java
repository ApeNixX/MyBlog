package com.apenixx.blog.service.impl;

import com.apenixx.blog.constant.SiteOwner;
import com.apenixx.blog.mapper.ResourceMapper;
import com.apenixx.blog.model.Resource;
import com.apenixx.blog.service.ResourceService;
import com.apenixx.blog.utils.BlogJSONResult;
import com.apenixx.blog.utils.StringUtil;
import com.apenixx.blog.utils.TimeUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author ApeNixX
 * @Date 2020/2/8 15:52
 * @Version 1.0
 * @Describe 资源业务实现
 */
@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceMapper resourceMapper;

    @Override
    public List<Resource> getAllResourceByType(String resourceTypeName,int pageNum,int rows) {
        PageHelper.startPage(pageNum,rows);
        if(StringUtil.BLANK.equals(resourceTypeName)){
          return resourceMapper.getAllResource1();
        }else {
          return resourceMapper.getAllResourceByType(resourceTypeName);
        }
    }

    @Override
    public BlogJSONResult insertResource(Resource resource) {
        TimeUtil timeUtil = new TimeUtil();
        resource.setCreateTime(timeUtil.getFormatDateForFive());
        resourceMapper.save(resource);
        return BlogJSONResult.ok();
    }

    @Override
    public BlogJSONResult updateResource(Resource resource, int id) {
        int i =resourceMapper.updateResource(resource,id);
        if(i==0){
            return BlogJSONResult.errorException("更新异常");
        }else {
            return BlogJSONResult.ok();
        }
    }

    @Override
    public BlogJSONResult changeReourceStatus(int status, int id) {

        int i = resourceMapper.updateResourceStatus(status,id);
        if(i==0){
            return BlogJSONResult.errorException("更新异常");
        }else {
            return BlogJSONResult.ok();
        }
    }

    @Override
    public List<Resource> getResourceList(int pageNum, int rows) {
        PageHelper.startPage(pageNum,rows);
        return resourceMapper.getAllResource();
    }
}
