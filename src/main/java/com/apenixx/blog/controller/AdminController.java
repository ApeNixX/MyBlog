package com.apenixx.blog.controller;

import com.apenixx.blog.mapper.CategoryMapper;
import com.apenixx.blog.mapper.HistoricalViewsMapper;
import com.apenixx.blog.mapper.UserMapper;
import com.apenixx.blog.model.*;
import com.apenixx.blog.service.ArticleService;
import com.apenixx.blog.service.CategoryService;
import com.apenixx.blog.service.RedisService;
import com.apenixx.blog.service.UserService;
import com.apenixx.blog.utils.BlogJSONResult;
import com.apenixx.blog.utils.TimeUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @Author ApeNixX
 * @Date 2020/1/31 20:47
 * @Version 1.0
 * @Describe
 */
@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    RedisService redisService;
    @Autowired
    HistoricalViewsMapper historicalViewsMapper;
    @RequestMapping("/index")
    public String index()
    {
        return "admin/admin";
    }

    @PostMapping("/getHistoricalViews")
    @ResponseBody
    public Map<String, Object> getHistoricalViews(){
        List<HistoricalViews> historicalViews = historicalViewsMapper.getHistoricalViews();
        Map<String,Object> map = new HashMap<>();
        TimeUtil timeUtil = new TimeUtil();
        //封装日期
        String [] timeArray = new String[historicalViews.size()];
        //封装人数
        int [] viewsArray = new int[historicalViews.size()];

        int i=0;

        for(HistoricalViews historicalViews1:historicalViews){
          timeArray[i] = timeUtil.getParseDateForYMD(historicalViews1.getCreateBy().toString());
          viewsArray[i] = historicalViews1.getViews();
          i++;
        }
        map.put("times",timeArray);
        map.put("views",viewsArray);

        return map;
    }



    @RequestMapping("/home")
    public String home(HttpServletRequest request){
        Long totalVisitor = redisService.getVisitorNumOnRedis("visitor", "totalVisitor");
        Long yesterdayVisitor = redisService.getVisitorNumOnRedis("visitor", "yesterdayVisitor");
        request.setAttribute("articleNum",articleService.countArticle());
        request.setAttribute("userNum",userService.countUserNum());
        request.setAttribute("tv",totalVisitor);
        request.setAttribute("yv",yesterdayVisitor);
        request.setAttribute("user",userMapper.getUserByRecent());
        return "admin/home";
    }
    @RequestMapping("/diary")
    public String diary(){
        return "admin/addnews";
    }
    @RequestMapping("/diarylist")
    public String diarylist(){
        return "admin/diaryList";
    }
    @RequestMapping("/resource")
    public String resource(){
        return "admin/addresource";
    }
    @RequestMapping("/resourcelist")
    public String resourcelist(){
        return "admin/resourceList";
    }
    @RequestMapping("/link")
    public String link(){
        return "admin/addlink";
    }
    @RequestMapping("/linklist")
    public String linklist(){
        return "admin/linkList";
    }
    @RequestMapping("/commentlist")
    public String commentlist(){
        return "admin/commentList";
    }
    @RequestMapping("/msglist")
    public String msglist(){
        return "admin/msgList";
    }
    @RequestMapping("/taglist")
    public String taglist(){
        return "admin/tagList";
    }
    @RequestMapping("/userlist")
    public String userlist(){
        return "admin/userList";
    }
    @RequestMapping("/rolelist")
    public String rolelist(){
        return "admin/roleList";
    }

    /**
     * 博客发布页面
     * @return
     */
    @RequestMapping("/editor")
    public String editor(){
        return "admin/editor";
    }
    /**
     * 博客草稿
     * @return
     */
    @RequestMapping("/draft")
    public String draft(HttpServletRequest request){
        String id = request.getParameter("id");
        if(!"".equals(id)){
            request.getSession().setAttribute("id", id);
        }
        return "admin/editor";
    }
    @RequestMapping("/bloglist")
    public String bloglist(){
        return "admin/newsList";
    }
    @RequestMapping("/categorylist")
    public String categorylist(){
        return "admin/categoryList";
    }

    @RequestMapping("/addcategory")
    public String addcategory(){
        return "admin/addcategory";
    }

    @RequestMapping("/getBlogList")
    @ResponseBody
    public Map<String,Object> getbloglist(@RequestParam("page") Integer page,@RequestParam("limit") Integer limit){
        PageInfo<Article> articlePageInfo = new PageInfo<>(articleService.getArticleManagement(limit,page));
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("code",0);
        map.put("data",articlePageInfo.getList());
        map.put("count",articlePageInfo.getTotal());
        return map;

    }

    @PostMapping("/deleteArticle")
    @ResponseBody
    public BlogJSONResult deleteArticle(@RequestParam("id") String articleId) {
        return articleService.deleteArticle(Long.parseLong(articleId));
    }

    @GetMapping(value = "/getArticleCategories")
    @ResponseBody
    public Map<String,Object> getArticleCategories(@RequestParam("page")Integer page,@RequestParam("limit")Integer limit){
        PageInfo<Categories> categoriesPageInfo = new PageInfo<>(categoryService.findAllCategories(limit,page));
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("code",0);
        map.put("data",categoriesPageInfo.getList());
        map.put("count",categoriesPageInfo.getTotal());
        return map;
    }

    @PostMapping(value = "/addArticleCategories")
    @ResponseBody
    public BlogJSONResult addArticleCategories(@RequestParam("categoryName") String categoryName){
            return categoryService.addCategory(categoryName);
    }
    @PostMapping("/updateArticleCategories")
    @ResponseBody
    public  BlogJSONResult updateArticleCategories(Categories categories){
        return categoryService.updateCategoryById(categories);
    }
    @PostMapping("/deleteArticleCategories")
    @ResponseBody
    public BlogJSONResult deleteArticleCategories(String categoryName) {
        return categoryService.deleteCategory(categoryName);
    }
    @GetMapping("/getCategories")
    @ResponseBody
    public BlogJSONResult getCategories(String categoryName) {
        return BlogJSONResult.ok(categoryMapper.findAllCategories());
    }

    @PostMapping("/searchArticle")
    @ResponseBody
    public Map<String,Object> searchArticle(String categoryName,Integer page,Integer limit) {
        PageInfo<Article> articlePageInfo = new PageInfo<>(articleService.findArticleByCategory(categoryName,limit,page));
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("code",0);
        map.put("data",articlePageInfo.getList());
        map.put("count",articlePageInfo.getTotal());
        return map;
    }



    @RequestMapping("/getUserList")
    @ResponseBody
    public Map<String,Object> getUser(int page,int limit) {
        PageInfo<User> userPageInfo = new PageInfo<>(userService.getAllUser(page,limit));
        Map<String,Object> map = new HashMap<>();
        map.put("code",0);
        map.put("data",userPageInfo.getList());
        map.put("count",userPageInfo.getTotal());
        return map;
    }

    @PostMapping("/updateUserStatus")
    @ResponseBody
    public BlogJSONResult updateUserStatus(int status,int id){
        return userService.updateUserStatus(status,id);
    }

    @PostMapping("/updateUser")
    @ResponseBody
    public BlogJSONResult updateUserStatus(User user,int id){
        userMapper.updateUser(user,id);
        return BlogJSONResult.ok();
    }

    @RequestMapping("/getRolesList")
    @ResponseBody
    public Map<String,Object> getRoles(int page,int limit) {
        PageInfo<User> userPageInfo = new PageInfo<>(userService.getAllUserAndRoles(page,limit));
        Map<String,Object> map = new HashMap<>();
        map.put("code",0);
        map.put("data",userPageInfo.getList());
        map.put("count",userPageInfo.getTotal());
        return map;
    }

    @PostMapping("/updateUserRole")
    @ResponseBody
    public BlogJSONResult updateUserRole(int roleId,int id){
        return userService.updateUserRoles(roleId, id);
    }

 }
