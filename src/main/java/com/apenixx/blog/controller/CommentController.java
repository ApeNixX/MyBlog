package com.apenixx.blog.controller;

import com.apenixx.blog.aspect.annotation.PermissionCheck;
import com.apenixx.blog.component.JavaScriptCheck;
import com.apenixx.blog.mapper.CommentMapper;
import com.apenixx.blog.model.Comment;
import com.apenixx.blog.model.Timeline;
import com.apenixx.blog.service.ArticleService;
import com.apenixx.blog.service.CommentService;
import com.apenixx.blog.service.UserService;
import com.apenixx.blog.utils.BlogJSONResult;
import com.apenixx.blog.utils.StringUtil;
import com.apenixx.blog.utils.TimeUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author ApeNixX
 * @Date 2020/2/4 14:50
 * @Version 1.0
 * @Describe
 */
@RestController
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    UserService userService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private CommentMapper commentMapper;
    /**
     * 获得该文章所有评论
     *
     * @param articleId 文章id
     * @return
     */
    @PostMapping("/getAllComment")
    public BlogJSONResult getAllComment(@RequestParam("articleId") Long articleId, @AuthenticationPrincipal Principal principal) {
        String username = null;
        if (principal != null) {
            username = principal.getName();
        }
        return commentService.findCommentByArticleId(articleId, username);
    }

    /**
     * 评论
     *
     * @param principal 当前用户
     */
    @PostMapping(value = "/publishComment")
    @PermissionCheck(value = "ROLE_USER")
    public BlogJSONResult publishComment(Comment comment, @AuthenticationPrincipal Principal principal, @RequestParam("articleId") Long articleId) {
        String publisher = principal.getName();
        TimeUtil timeUtil = new TimeUtil();
        //文章作者名
        String username = articleService.getArticleAuthorById(articleId);
        comment.setCommentDate(timeUtil.getFormatDateForFive());
        int userId = userService.findIdByUsername(publisher);
        comment.setAnswererId(userId);
        comment.setRespondentId(userService.findIdByUsername(username));
        comment.setCommentContent(JavaScriptCheck.javaScriptCheck(comment.getCommentContent()));

        commentService.insertComment(comment);

        BlogJSONResult result = commentService.findCommentByArticleId(comment.getArticleId(), publisher);

        return result;
    }

    /**
     * 评论中的回复
     *
     * @param principal 当前用户
     */
    @PostMapping(value = "/publishReply")
    @PermissionCheck(value = "ROLE_USER")
    public BlogJSONResult publishReply(Comment comment,
                                       @RequestParam("parentId") String parentId,
                                       @RequestParam("respondent") String respondent,
                                       @AuthenticationPrincipal Principal principal) {
        String username = principal.getName();
        comment.setPId(Long.parseLong(parentId.substring(1)));
        comment.setAnswererId(userService.findIdByUsername(username));
        comment.setRespondentId(userService.findIdByUsername(respondent));
        TimeUtil timeUtil = new TimeUtil();
        comment.setCommentDate(timeUtil.getFormatDateForFive());
        String commentContent = comment.getCommentContent();
        comment.setCommentContent(commentContent.trim());
        //判断用户输入内容是否为空字符串
        if (StringUtil.BLANK.equals(comment.getCommentContent())) {
            return BlogJSONResult.errorMsg("内容为空");
        } else {
            //防止xss攻击
            comment.setCommentContent(JavaScriptCheck.javaScriptCheck(comment.getCommentContent()));
            commentService.insertComment(comment);
        }
        BlogJSONResult data = commentService.findCommentByArticleId(comment.getArticleId(),username);
        return data;
    }

    @RequestMapping("/getCommentList")
    public Map<String,Object> getCommentList(int page, int limit) {
        PageInfo<Comment> commentPageInfo = new PageInfo<>(commentService.getAllComment(page,limit));
        Map<String,Object> map = new HashMap<>();
        map.put("code",0);
        map.put("data",commentPageInfo.getList());
        map.put("count",commentPageInfo.getTotal());
        return map;
    }

    @PostMapping("/deleteComment")
    public BlogJSONResult deleteComment(int id){
        commentMapper.deleteComment(id);
        return BlogJSONResult.ok();
    }

}
