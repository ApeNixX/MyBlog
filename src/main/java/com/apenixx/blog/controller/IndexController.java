package com.apenixx.blog.controller;

import com.apenixx.blog.mapper.ArticleMapper;
import com.apenixx.blog.service.VisitorService;
import com.apenixx.blog.utils.BlogJSONResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author ApeNixX
 * @Date 2020/2/11 17:45
 * @Version 1.0
 * @Describe
 */
@Controller
@Slf4j
public class IndexController {
    @Autowired
    VisitorService visitorService;
    @Autowired
    ArticleMapper articleMapper;

    @RequestMapping("/")
    public String index(HttpServletRequest request){
        request.setAttribute("articles",articleMapper.getIndexArticleByLooks());
        return "index";
    }

    /**
     * 增加访客量
     * @return  网站总访问量以及访客量
     */
    @GetMapping(value = "/getVisitorNumByPageName")
    @ResponseBody
    public BlogJSONResult getVisitorNumByPageName(HttpServletRequest request,
                                          @RequestParam("pageName") String pageName){
        try {
            int index = pageName.indexOf("/");
            if(index == -1){
                pageName = "visitorVolume";
            }
            if(pageName.contains("tag")){
                return BlogJSONResult.errorException("no");
            }
            BlogJSONResult data = visitorService.addVisitorNumByPageName(pageName, request);
            return data;
        } catch (Exception e){
            log.error("pageName [{}] get visitor num exception", pageName, e);
        }
        return BlogJSONResult.errorException("获取访客异常！");
    }
}
