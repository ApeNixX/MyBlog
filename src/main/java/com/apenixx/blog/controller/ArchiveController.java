package com.apenixx.blog.controller;

import com.apenixx.blog.service.ArchiveService;
import com.apenixx.blog.service.ArticleService;
import com.apenixx.blog.utils.BlogJSONResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author ApeNixX
 * @Date 2020/1/30 17:03
 * @Version 1.0
 * @Describe
 */
@RestController
public class ArchiveController {
    @Autowired
    ArchiveService archiveService;
    @Autowired
    ArticleService articleService;

    /**
     * 获得所有归档日期以及每个归档日期的文章数目
     * @return
     */
    @GetMapping("/findArchiveNameAndArticleNum")
    public BlogJSONResult findArchiveNameAndArticleNum(){
      return  archiveService.findArchiveNameAndArticleNum();
    }

    /**
     * 分页获得该归档日期下的文章
     */
    @GetMapping("/getArchiveArticle")
    public BlogJSONResult getArchiveArticle(@RequestParam("archive")String archive, HttpServletRequest request){
        int rows = Integer.parseInt(request.getParameter("rows"));
        int pageNum = Integer.parseInt(request.getParameter("pageNum"));
        return articleService.findArticleByArchive(archive,rows,pageNum);
    }
}
