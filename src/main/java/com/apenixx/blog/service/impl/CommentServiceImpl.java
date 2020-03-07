package com.apenixx.blog.service.impl;

import com.apenixx.blog.mapper.CommentMapper;
import com.apenixx.blog.mapper.UserMapper;
import com.apenixx.blog.model.Comment;
import com.apenixx.blog.model.UserReadNews;
import com.apenixx.blog.model.VO.CommentVO;
import com.apenixx.blog.redis.HashRedisServiceImpl;
import com.apenixx.blog.service.ArticleService;
import com.apenixx.blog.service.CommentService;
import com.apenixx.blog.service.UserService;
import com.apenixx.blog.utils.BlogJSONResult;
import com.apenixx.blog.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author ApeNixX
 * @Date 2020/2/4 13:48
 * @Version 1.0
 * @Describe
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    HashRedisServiceImpl hashRedisServiceImpl;
    @Autowired
    UserMapper userMapper;

    @Override
    public BlogJSONResult findCommentByArticleId(long articleId, String username) {
        List<Comment> commentLists = commentMapper.findCommentByArticleIdAndPid(articleId, 0);
        JSONArray commentJsonArray = new JSONArray();
        JSONArray replyJsonArray;
        JSONObject commentJsonObject;
        JSONObject replyJsonObject;
        List<Comment> replyLists;
        for(Comment comment:commentLists){
            commentJsonObject = new JSONObject();
            replyLists = commentMapper.findCommentByArticleIdAndPidNoOrder(articleId,comment.getId());

            replyJsonArray = new JSONArray();
            //封装所有评论中的回复
            for(Comment reply:replyLists){
                replyJsonObject = new JSONObject();
                replyJsonObject.put("id",reply.getId());
                replyJsonObject.put("answerer",userService.findUsernameById(reply.getAnswererId()));
                replyJsonObject.put("avatarImgUrl",userService.getHeadPortraitUrlByUserId(reply.getAnswererId()));
                replyJsonObject.put("commentDate",reply.getCommentDate());
                replyJsonObject.put("commentContent",reply.getCommentContent());
                replyJsonObject.put("respondent",userService.findUsernameById(reply.getRespondentId()));
                replyJsonObject.put("location",reply.getLocation());
                if (userMapper.getRoleNumByUserId(reply.getAnswererId())>2){
                    replyJsonObject.put("master",1);
                }else {
                    replyJsonObject.put("master",0);
                }
                replyJsonArray.add(replyJsonObject);
            }

            //封装评论
            commentJsonObject.put("id",comment.getId());
            commentJsonObject.put("answerer",userService.findUsernameById(comment.getAnswererId()));
            commentJsonObject.put("commentDate",comment.getCommentDate());
            commentJsonObject.put("likes",comment.getLikes());
            commentJsonObject.put("commentContent",comment.getCommentContent());
            commentJsonObject.put("replies",replyJsonArray);
            commentJsonObject.put("avatarImgUrl",userService.getHeadPortraitUrlByUserId(comment.getAnswererId()));
            commentJsonObject.put("location",comment.getLocation());
            if (userMapper.getRoleNumByUserId(comment.getAnswererId())>2){
                commentJsonObject.put("master",1);
            }else {
                commentJsonObject.put("master",0);
            }
            commentJsonArray.add(commentJsonObject);
        }


        return BlogJSONResult.ok(commentJsonArray);
    }

    @Override
    public void insertComment(Comment comment) {
        if(comment.getAnswererId() == comment.getRespondentId()){
            comment.setIsRead(0);
        }
        commentMapper.save(comment);
        //redis中保存该用户未读消息
        addNotReadNews(comment);
    }

    @Override
    public BlogJSONResult replyReplyReturn(Comment comment, String answerer, String respondent) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id",comment.getId());
        jsonObject.put("answerer",answerer);
        jsonObject.put("respondent", respondent);
        jsonObject.put("avatarImgUrl",userService.getHeadPortraitUrlByUserId(comment.getAnswererId()));
        jsonObject.put("commentContent", comment.getCommentContent());
        jsonObject.put("commentDate", comment.getCommentDate());
        return BlogJSONResult.ok(jsonObject);
    }

    @Override
    public BlogJSONResult getUserComment(int rows, int pageNum, String username) {
        int userId = userService.findIdByUsername(username);
        PageHelper.startPage(pageNum, rows);
        List<Comment> comments = commentMapper.getUserCommentByRespondentId(userId);
        PageInfo<Comment> pageInfo = new PageInfo<>(comments);

        JSONObject returnJson = new JSONObject();
        JSONObject commentJson;
        JSONArray commentJsonArray = new JSONArray();
        for(Comment comment : comments){
            commentJson = new JSONObject();
            commentJson.put("id",comment.getId());
            commentJson.put("pId",comment.getPId());
            commentJson.put("articleId",comment.getArticleId());
            commentJson.put("articleTitle",articleService.findArticleTitleByArticleId(comment.getArticleId()).get("articleTitle"));
            commentJson.put("answerer", userService.findUsernameById(comment.getAnswererId()));
            commentJson.put("commentDate",comment.getCommentDate());
            commentJson.put("isRead",comment.getIsRead());
            commentJsonArray.add(commentJson);

        }
        returnJson.put("result",commentJsonArray);
        returnJson.put("msgIsNotReadNum",commentMapper.countIsReadNumByRespondentId(userId));

        JSONObject pageJson = new JSONObject();
        pageJson.put("pageNum",pageInfo.getPageNum());
        pageJson.put("pageSize",pageInfo.getPageSize());
        pageJson.put("total",pageInfo.getTotal());
        pageJson.put("pages",pageInfo.getPages());
        pageJson.put("isFirstPage",pageInfo.isIsFirstPage());
        pageJson.put("isLastPage",pageInfo.isIsLastPage());
        returnJson.put("pageInfo",pageJson);

        return BlogJSONResult.ok(returnJson);
    }

    @Override
    public BlogJSONResult readOneCommentRecord(int id) {
        try {
            commentMapper.readCommentRecordById(id);
            return BlogJSONResult.ok();
        } catch (Exception e){
            e.printStackTrace();
            return BlogJSONResult.errorMsg("已读信息失败");
        }    }

    @Override
    public BlogJSONResult readAllComment(String username) {
        int respondentId = userService.findIdByUsername(username);
        commentMapper.readCommentRecordByRespondentId(respondentId);
        return BlogJSONResult.ok();
    }

    @Override
    public List<CommentVO> findFiveNewComment() {
        List<Comment> comments = commentMapper.findFiveNewComment();
        Map<String,Object> map = new HashMap<>();
        List<CommentVO> commentVOS = new ArrayList<>();
        for(Comment comment:comments){
            CommentVO commentVO = new CommentVO();
            commentVO.setArticleId(comment.getArticleId());
            commentVO.setName(userService.findUsernameById(comment.getAnswererId()));
            commentVO.setPId(comment.getPId());
            commentVO.setImg(userService.getHeadPortraitUrlByUserId(comment.getAnswererId()));
            commentVO.setContent(comment.getCommentContent());
            commentVO.setTime(comment.getCommentDate());
            commentVO.setArticleName(articleService.findArticleTitleByArticleId(comment.getArticleId()).get("articleTitle"));
            commentVOS.add(commentVO);
        }
        return commentVOS;
    }

    @Override
    public List<Comment> getAllComment(int pageNum, int rows) {
        PageHelper.startPage(pageNum,rows);
        return commentMapper.getAllComment();
    }


    /**
     * 保存评论成功后往redis中增加一条未读评论数
     */
    private void addNotReadNews(Comment comment){
        if(comment.getRespondentId() != comment.getAnswererId()) {
            boolean isExistKey = hashRedisServiceImpl.hasKey(comment.getRespondentId()+ StringUtil.BLANK);
            if(!isExistKey){
                UserReadNews news = new UserReadNews(1,1,0);
                hashRedisServiceImpl.put(String.valueOf(comment.getRespondentId()), news, UserReadNews.class);
            } else {
                hashRedisServiceImpl.hashIncrement(comment.getRespondentId()+ StringUtil.BLANK, "allNewsNum",1);
                hashRedisServiceImpl.hashIncrement(comment.getRespondentId()+ StringUtil.BLANK, "commentNum",1);
            }
        }
    }
}
