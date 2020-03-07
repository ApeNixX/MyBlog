package com.apenixx.blog.controller;

import com.apenixx.blog.component.StringAndArray;
import com.apenixx.blog.model.Article;
import com.apenixx.blog.service.ArticleService;
import com.apenixx.blog.service.CategoryService;
import com.apenixx.blog.service.TagService;
import com.apenixx.blog.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author ApeNixX
 * @Date 2020/1/27 18:33
 * @Version 1.0
 * @Describe
 */
@Controller
@Slf4j
public class EditorControl {


    @Autowired
    private ArticleService articleService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    TagService tagService;

    @RequestMapping(value = "/publishArticle", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public BlogJSONResult publishArticle(Article article, HttpServletRequest request, @RequestParam("articleGrade") String articleGrade, @AuthenticationPrincipal Principal principal){
        //获得文章html代码并生成摘要
        BuildArticleTabloidUtil buildArticleTabloidUtil = new BuildArticleTabloidUtil();
        String articleHtmlContent = buildArticleTabloidUtil.buildArticleTabloid(request.getParameter("articleHtmlContent"));
        article.setArticleTabloid(articleHtmlContent+"...");
        String[] articleTags = request.getParameterValues("articleTagsValue");

        for(int i=0;i<articleTags.length;i++){
            //去掉可能出现的换行符
            articleTags[i] = articleTags[i].replaceAll("<br>", StringUtil.BLANK).replaceAll("&nbsp;", StringUtil.BLANK).replaceAll("\\s+", StringUtil.BLANK).trim();

        }
        //添加标签
        tagService.addTags(articleTags, Integer.parseInt(articleGrade));

        //标签数组转成字符串
        String label = StringAndArray.arrayToString(articleTags);
        article.setArticleTags(label);
        article.setAuthor(principal.getName());

        TimeUtil timeUtil = new TimeUtil();
        String nowDate = timeUtil.getFormatDateForFive();
        long articleId = timeUtil.getLongTime();

        article.setArticleId(articleId);
        article.setPublishDate(nowDate);
        article.setUpdateDate(nowDate);
        articleService.insertArticle(article);
        return BlogJSONResult.ok(article);
    }

    @GetMapping(value = "/findCategoriesName")
    @ResponseBody
    public BlogJSONResult findCategoriesName(){
        return BlogJSONResult.ok(categoryService.findCategoriesName());
    }




    /**
     * 文章编辑本地上传图片
     */
    @RequestMapping("/uploadImage")
    @ResponseBody
    public Map<String,Object> uploadImage(HttpServletRequest request, HttpServletResponse response,
                                          @RequestParam(value = "editormd-image-file",required = false) MultipartFile file){
        Map<String,Object> resultMap = new HashMap<String,Object>();
        try {
            request.setCharacterEncoding( "utf-8" );
            //设置返回头后页面才能获取返回url
            response.setHeader("X-Frame-Options", "SAMEORIGIN");

            FileUtil fileUtil = new FileUtil();
            //文件路径
              //String filePath = this.getClass().getResource("/").getPath().substring(6) + "blogImg/";

           String filePath = this.getClass().getResource("/").getPath().substring(1) + "blogImg/";
            log.info(this.getClass().getResource("/").getPath());
            String fileContentType = file.getContentType();
            //文件后缀
            String fileExtension = fileContentType.substring(fileContentType.indexOf("/") + 1);

            TimeUtil timeUtil = new TimeUtil();
            //文件名
            String fileName = timeUtil.getLongTime() + "." + fileExtension;

            //文件目录
            String subCatalog = "blogArticles/" + new TimeUtil().getFormatDateForThree();
            String fileUrl = fileUtil.uploadFile(fileUtil.multipartFileToFile(file, filePath, fileName), subCatalog);
            log.info("文件路径"+filePath+"文件后缀"+fileContentType+"文件后缀"+fileExtension+"文件名"+fileName+"文件目录"+subCatalog+"文件URL"+fileUrl);
            resultMap.put("success",1);
            resultMap.put("message", "上传成功");
            resultMap.put("url", fileUrl);

        }catch (Exception e){
            e.printStackTrace();
        }
        return  resultMap;
    }

    /**
     * 上传封面
     */
    @RequestMapping("/image")
    @ResponseBody
    public Map<String,Object> image(@RequestParam("imageFile") MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            request.setCharacterEncoding("utf-8");
            //设置返回头后页面才能获取返回url
            response.setHeader("X-Frame-Options", "SAMEORIGIN");

            FileUtil fileUtil = new FileUtil();
            //文件路径
            //String filePath = this.getClass().getResource("/").getPath().substring(6) + "blogImg/";

           String filePath = this.getClass().getResource("/").getPath().substring(1) + "blogImg/";
            log.info(this.getClass().getResource("/").getPath());
            String fileContentType = file.getContentType();
            //文件后缀
            String fileExtension = fileContentType.substring(fileContentType.indexOf("/") + 1);

            TimeUtil timeUtil = new TimeUtil();
            //文件名
            String fileName = timeUtil.getLongTime() + "." + fileExtension;

            //文件目录
            String subCatalog = "blogArticlesCover/" + new TimeUtil().getFormatDateForThree();
            String fileUrl = fileUtil.uploadFile(fileUtil.multipartFileToFile(file, filePath, fileName), subCatalog);
            log.info("文件路径" + filePath + "文件后缀" + fileContentType + "文件后缀" + fileExtension + "文件名" + fileName + "文件目录" + subCatalog + "文件URL" + fileUrl);
            resultMap.put("success", 1);
            resultMap.put("message", "上传成功");
            resultMap.put("url", fileUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  resultMap;

    }

    @GetMapping("/getDraftArticle")
    @ResponseBody
    public BlogJSONResult editBlog(HttpServletRequest request) {
        String id = (String) request.getSession().getAttribute("id");
        //判断是否为修改文章
        if(id != null) {
            request.getSession().removeAttribute("id");
         Article article =  articleService.findArticleById(Integer.parseInt(id));
         String[] labels = StringAndArray.stringToArray(article.getArticleTags());
         article.setTagValue(labels);
         return BlogJSONResult.build(201, "编辑博客",article);
        }
        return BlogJSONResult.ok();
    }

    @PostMapping("/updateArticle")
    @ResponseBody
    public BlogJSONResult updateArticle(Article article,@AuthenticationPrincipal Principal principal){
        String[] articleTags = article.getTagValue();

        for(int i=0;i<articleTags.length;i++){
            //去掉可能出现的换行符
            articleTags[i] = articleTags[i].replaceAll("<br>", StringUtil.BLANK).replaceAll("&nbsp;", StringUtil.BLANK).replaceAll("\\s+", StringUtil.BLANK).trim();

        }
        //标签数组转成字符串
        String label = StringAndArray.arrayToString(articleTags);
        article.setArticleTags(label);
        article.setAuthor(principal.getName());
        TimeUtil timeUtil = new TimeUtil();
        String nowDate = timeUtil.getFormatDateForFive();
        article.setUpdateDate(nowDate);
        return articleService.updateArticleById(article);
    }

}
