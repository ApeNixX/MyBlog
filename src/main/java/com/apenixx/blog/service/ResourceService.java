package com.apenixx.blog.service;

import com.apenixx.blog.model.Resource;
import com.apenixx.blog.utils.BlogJSONResult;

import java.util.List;

/**
 * @Author ApeNixX
 * @Date 2020/2/8 15:51
 * @Version 1.0
 * @Describe
 */
public interface ResourceService {
    List<Resource> getAllResourceByType(String resourceTypeName,int pageNum,int rows);
    BlogJSONResult insertResource(Resource resource);
    BlogJSONResult updateResource(Resource resource,int id);
    BlogJSONResult changeReourceStatus(int status,int id);
    List<Resource> getResourceList(int pageNum, int rows);
}
