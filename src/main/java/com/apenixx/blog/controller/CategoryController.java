package com.apenixx.blog.controller;

import com.apenixx.blog.mapper.ArticleMapper;
import com.apenixx.blog.model.Article;
import com.apenixx.blog.model.Categories;
import com.apenixx.blog.service.ArticleService;
import com.apenixx.blog.service.CategoryService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author ApeNixX
 * @Date 2020/1/29 18:29
 * @Version 1.0
 * @Describe
 */
@Controller
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private ArticleMapper articleMapper;
    @GetMapping("/category/{pageNum}")
    public String index(HttpServletRequest request, Model model, @RequestParam(value = "category",defaultValue = "") String category,
                        @PathVariable("pageNum") Integer pageNum){
        request.setAttribute("categories",categoryService.findCategoriesName());
        List<Article> articles = articleService.findArticleByCategory(category,8,pageNum);
        PageInfo<Article> articlePageInfo = new PageInfo<>(articles);
        model.addAttribute("pageInfo",articlePageInfo);
        model.addAttribute("p1",category);
        request.setAttribute("looks",articleMapper.getArticleByLooks());

        return "article";
    }

}
