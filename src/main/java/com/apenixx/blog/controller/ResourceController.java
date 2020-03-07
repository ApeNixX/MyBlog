package com.apenixx.blog.controller;

import com.apenixx.blog.constant.SiteOwner;
import com.apenixx.blog.model.Resource;
import com.apenixx.blog.service.ResourceService;
import com.apenixx.blog.utils.BlogJSONResult;
import com.apenixx.blog.utils.FileUtil;
import com.apenixx.blog.utils.TimeUtil;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author ApeNixX
 * @Date 2020/2/8 16:07
 * @Version 1.0
 * @Describe 资源控制层
 */
@RestController
@Slf4j
public class ResourceController {
    @Autowired
    private ResourceService resourceService;

    @RequestMapping("/getResourceList")
    public Map<String,Object> getResourceList(int page,int limit){
        PageInfo<Resource> resourcePageInfo = new PageInfo<>(resourceService.getResourceList(page,limit));
        Map<String,Object> map = new HashMap<>();
        map.put("code",0);
        map.put("data",resourcePageInfo.getList());
        map.put("count",resourcePageInfo.getTotal());
        return  map;
    }

    @PostMapping("/getAllResource")
    public Map<String,Object> getAllResource(String resourceTypeName,int pageIndex){
        PageInfo<Resource> resourcePageInfo = new PageInfo<>(resourceService.getAllResourceByType(resourceTypeName,pageIndex,8));
        Map<String,Object> map = new HashMap<>();
        map.put("code",1);
        map.put("resourceSharings",resourcePageInfo.getList());
        map.put("currPage",resourcePageInfo.getPageNum());
        map.put("pages",resourcePageInfo.getPages());
        return  map;
    }

    @PostMapping("/publishResource")
    public BlogJSONResult publishResource(Resource resource,@AuthenticationPrincipal Principal principal){
        resource.setResourceUserName(principal.getName());
        return resourceService.insertResource(resource);
    }

    @PostMapping("/updateResource")
    public BlogJSONResult updateResource(Resource resource,int id){
        return resourceService.updateResource(resource,id);
    }

    @PostMapping("/updateResourceStatus")
    public BlogJSONResult updateResourceStatus(int status,int id){
        return resourceService.changeReourceStatus(status,id);
    }

    @RequestMapping("/uploadResourceImg")
    @ResponseBody
    public Map<String,Object> image(@RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Map<String, Object> map2 = new HashMap<>();

        try {
            request.setCharacterEncoding("utf-8");
            //设置返回头后页面才能获取返回url
            response.setHeader("X-Frame-Options", "SAMEORIGIN");

            FileUtil fileUtil = new FileUtil();
            //文件路径
            String filePath = this.getClass().getResource("/").getPath().substring(1) + "blogImg/";
            log.info(this.getClass().getResource("/").getPath());
            String fileContentType = file.getContentType();
            //文件后缀
            String fileExtension = fileContentType.substring(fileContentType.indexOf("/") + 1);

            TimeUtil timeUtil = new TimeUtil();
            //文件名
            String fileName = timeUtil.getLongTime() + "." + fileExtension;

            //文件目录
            String subCatalog = "ResourceImg/" + new TimeUtil().getFormatDateForThree();
            String fileUrl = fileUtil.uploadFile(fileUtil.multipartFileToFile(file, filePath, fileName), subCatalog);
            log.info("文件路径" + filePath + "文件后缀" + fileContentType + "文件后缀" + fileExtension + "文件名" + fileName + "文件目录" + subCatalog + "文件URL" + fileUrl);
            resultMap.put("code", 0);
            resultMap.put("msg", "上传成功");
            resultMap.put("data",map2);
            map2.put("src", fileUrl);//图片url
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  resultMap;

    }
}
