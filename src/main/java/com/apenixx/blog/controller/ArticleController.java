package com.apenixx.blog.controller;

import com.apenixx.blog.aspect.annotation.PermissionCheck;
import com.apenixx.blog.mapper.ArticleMapper;
import com.apenixx.blog.mapper.CommentMapper;
import com.apenixx.blog.mapper.LeaveMessageMapper;
import com.apenixx.blog.model.Article;
import com.apenixx.blog.model.ArticleLikesRecord;
import com.apenixx.blog.service.*;
import com.apenixx.blog.utils.BlogJSONResult;
import com.apenixx.blog.utils.StringUtil;
import com.apenixx.blog.utils.TimeUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.List;

/**
 * @Author ApeNixX
 * @Date 2020/1/20 13:14
 * @Version 1.0
 * @Describe
 */
@Controller
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private TagService tagService;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private LeaveMessageMapper leaveMessageMapper;
    @Autowired
    private FriendLinkService friendLinkService;
    @Autowired
    private CommentService commentService;
    @Autowired
    ArticleMapper articleMapper;
    @Autowired
    ArticleLikesRecordService articleLikesRecordService;

    @Autowired
    UserService userService;
    @Autowired
    RedisService redisService;

    @GetMapping("/index/{pageNum}")
    public String index(HttpServletRequest request, @PathVariable("pageNum") Integer pageNum,
                        @RequestParam(value = "tag",defaultValue = "")String tag){
        List<Article> articles = articleService.findArticleByTag(tag,12,pageNum);
        PageInfo<Article> pageInfo = new PageInfo<>(articles);
        request.setAttribute("pageInfo",pageInfo);
        request.setAttribute("articleNum",articleService.countArticle());
        request.setAttribute("tagNum",tagService.countTagsNum());
        request.setAttribute("tags",tagService.findTagsCloud());
        request.setAttribute("commentNum",commentMapper.countTotalCommentNum());
        request.setAttribute("msgNum",leaveMessageMapper.countTotalMsgNum());
        request.setAttribute("links",friendLinkService.getAllFriendLink());
        request.setAttribute("comments",commentService.findFiveNewComment());
        request.setAttribute("looks",articleMapper.getArticleByLooks());
        request.setAttribute("tag",tag);

        return "home";
    }

    @GetMapping("/details/{articleId}")
    public String details(@PathVariable("articleId") Integer articleId,@AuthenticationPrincipal Principal principal,HttpServletRequest request, HttpServletResponse response){
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        String username = null;
        if(principal != null){
            username = principal.getName();
        }
        request.setAttribute("info",articleService.getArticlesByArticleId(articleId,username));
        request.setAttribute("tags",tagService.findTagsCloud());
        request.setAttribute("time",articleMapper.getArticleByTime());
        request.setAttribute("rand",articleMapper.getArticleByRand());
        //将文章id存入响应头
        response.setHeader("articleId",String.valueOf(articleId));
        return "detail";
    }

    /**
     * 点赞
     * @param articleId 文章号
     */
    @GetMapping(value = "/addArticleLike")
    @PermissionCheck(value = "ROLE_USER")
    @ResponseBody
    public BlogJSONResult addArticleLike(@RequestParam("articleId") String articleId,
                                         @AuthenticationPrincipal Principal principal){
        String username = principal.getName();
        if(articleLikesRecordService.isLiked(Long.parseLong(articleId), username)){
            return BlogJSONResult.errorMsg("你已点赞过啦~");
        }
        BlogJSONResult data = articleService.updateLikeByArticleId(Long.parseLong(articleId));

        ArticleLikesRecord articleLikesRecord = new ArticleLikesRecord(Long.parseLong(articleId), userService.findIdByUsername(username), new TimeUtil().getFormatDateForFive());
        articleLikesRecordService.insertArticleLikesRecord(articleLikesRecord);
        redisService.readThumbsUpRecordOnRedis(StringUtil.ARTICLE_THUMBS_UP, 1);

        return  data;
    }

}
