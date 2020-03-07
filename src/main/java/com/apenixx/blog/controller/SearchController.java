package com.apenixx.blog.controller;

import com.apenixx.blog.mapper.ArticleMapper;
import com.apenixx.blog.model.Article;
import com.apenixx.blog.service.ArticleService;
import com.apenixx.blog.service.CategoryService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author ApeNixX
 * @Date 2020/2/13 19:11
 * @Version 1.0
 * @Describe
 */
@Controller
public class SearchController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private ArticleMapper articleMapper;

//    @RequestMapping(value = {"/searchArticle/{pageNum}","/searchArticle/{pageNum}/{searchParam}"})
//    public String searchArticle(@PathVariable(required = false)String searchParam, HttpServletRequest request,@PathVariable(value = "pageNum") Integer pageNum){
//        request.setAttribute("categories",categoryService.findCategoriesName());
//        List<Article> articles = articleService.searchArticle(searchParam,pageNum,10);
//        PageInfo<Article> articlePageInfo = new PageInfo<>(articles);
////        request.setAttribute("param1",searchParam);
//        request.setAttribute("pageInfo",articlePageInfo);
//        request.setAttribute("looks",articleMapper.getArticleByLooks());
//        return "search";
//    }

    @RequestMapping("/searchArticle/{pageNum}")
    public String searchArticle(@RequestParam(value = "searchParam",defaultValue = "")String searchParam, HttpServletRequest request,@PathVariable(value = "pageNum") Integer pageNum){
        request.setAttribute("categories",categoryService.findCategoriesName());
        List<Article> articles = articleService.searchArticle(searchParam,pageNum,8);
        PageInfo<Article> articlePageInfo = new PageInfo<>(articles);
        request.setAttribute("p1",searchParam);
        request.setAttribute("pageInfo",articlePageInfo);
        request.setAttribute("looks",articleMapper.getArticleByLooks());
        return "search";
    }
}
