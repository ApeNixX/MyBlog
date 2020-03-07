package com.apenixx.blog.service.impl;

import com.apenixx.blog.component.StringAndArray;
import com.apenixx.blog.constant.SiteOwner;
import com.apenixx.blog.mapper.ArticleLikesMapper;
import com.apenixx.blog.mapper.ArticleMapper;
import com.apenixx.blog.mapper.CommentMapper;
import com.apenixx.blog.model.Article;
import com.apenixx.blog.service.ArchiveService;
import com.apenixx.blog.service.ArticleService;
import com.apenixx.blog.service.RedisService;
import com.apenixx.blog.service.VisitorService;
import com.apenixx.blog.utils.BlogJSONResult;
import com.apenixx.blog.utils.MarkDownToHtmlUtil;
import com.apenixx.blog.utils.StringUtil;
import com.apenixx.blog.utils.TimeUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author ApeNixX
 * @Date 2020/1/20 14:28
 * @Version 1.0
 * @Describe
 */
@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArchiveService archiveService;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private VisitorService visitorService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private ArticleLikesMapper articleLikesMapper;
    @Override
    public List<Article> findAllArticles(Integer rows, Integer pageNo) {
        PageHelper.startPage(pageNo,rows);
        List<Article> articles = articleMapper.findAllArticles();
        for(Article article:articles){
            article.setTagValue(StringAndArray.stringToArray(article.getArticleTags()));
        }
        return articles;
    }

    @Override
    public Article getArticlesByArticleId(long articleId, String username) {
        Article article = articleMapper.getArticleByArticleId(articleId);
        article.setArticleContent(MarkDownToHtmlUtil.mdToHtml(article.getArticleContent()));
        article.setTagValue(StringAndArray.stringToArray(article.getArticleTags()));
        article.setLooks(redisService.getVisitorNumOnRedis("visitor","article/details/"+article.getArticleId()));
        return article;
    }

    @Override
    public void insertArticle(Article article) {
        try {
            if(StringUtil.BLANK.equals(article.getOriginalAuthor())){
                article.setOriginalAuthor(article.getAuthor());
            }
            if(StringUtil.BLANK.equals(article.getArticleUrl())){
                //保存文章的url
                String url = SiteOwner.SITE_OWNER_URL + "/article/details/" + article.getArticleId();
                article.setArticleUrl(url);
            }
            Article endArticleId = articleMapper.findEndArticleId();
            //设置文章的上一篇文章id
            if(endArticleId != null){
                article.setLastArticleId(endArticleId.getArticleId());
            }
            //判断发表文章的归档日期是否存在，不存在则插入归档日期
            TimeUtil timeUtil = new TimeUtil();
            String archiveName =  timeUtil.timeWhippletreeToYear(article.getPublishDate().substring(0, 7));
            archiveService.addArchiveName(archiveName);
            //新文章加入访客量
            visitorService.insertVisitorArticlePage("article/details/" + article.getArticleId());
            //访客量存入redis
            redisService.putVisitorNumOnRedis("visitor","article/details/"+article.getArticleId(),0);

            articleMapper.save(article);

        }catch (Exception e){
            e.printStackTrace();
//                 return BlogJSONResult.errorException("异常");
        }
    }

    @Override
    public List<Article> findArticleByCategory(String category, int rows, int pageNum) {
        List<Article> articles;

        PageHelper.startPage(pageNum,rows);
        if(StringUtil.BLANK.equals(category)){
            articles=articleMapper.findAllArticles();
            category = "全部分类";
        }else{
            articles = articleMapper.findArticleByCategory(category);
        }
        for(Article article:articles){
            article.setTagValue(StringAndArray.stringToArray(article.getArticleTags()));
            article.setCommentNum(commentMapper.countNumByArticleId(article.getArticleId()));
            if(redisService.getVisitorNumOnRedis("visitor","article/details/"+article.getArticleId())==null){
                redisService.putVisitorNumOnRedis("visitor","article/details/"+article.getArticleId(),0);
            }
            article.setLooks(redisService.getVisitorNumOnRedis("visitor","article/details/"+article.getArticleId()));
        }
        return articles;
    }

    @Override
    public List<Article> findArticleByTag(String tag, int rows, int pageNum) {
        List<Article> articles;
        PageHelper.startPage(pageNum,rows);
        if(StringUtil.BLANK.equals(tag)){
            articles=articleMapper.findAllArticles();
        }else{
            articles = articleMapper.findArticleByTag(tag);

        }
        for(Article article:articles){
            article.setTagValue(StringAndArray.stringToArray(article.getArticleTags()));
            article.setCommentNum(commentMapper.countNumByArticleId(article.getArticleId()));
            if(redisService.getVisitorNumOnRedis("visitor","article/details/"+article.getArticleId())==null){
                redisService.putVisitorNumOnRedis("visitor","article/details/"+article.getArticleId(),0);
            }
            article.setLooks(redisService.getVisitorNumOnRedis("visitor","article/details/"+article.getArticleId()));
        }
        return articles;
    }

    @Override
    public BlogJSONResult findArticleByArchive(String archive, int rows, int pageNum) {
        List<Article> articles;
        PageInfo<Article> pageInfo;
        JSONArray articleJsonArray = new JSONArray();
        TimeUtil timeUtil = new TimeUtil();
        String showMonth = "hide";
        if(!StringUtil.BLANK.equals(archive)){
            archive = timeUtil.timeYearToWhippletree(archive);
        }
        PageHelper.startPage(pageNum,rows);
        if(StringUtil.BLANK.equals(archive)){
            articles = articleMapper.findAllArticles();
        } else {
            articles = articleMapper.findArticleByArchive(archive);
            showMonth = "show";
        }
         pageInfo = new PageInfo<>(articles);
        articleJsonArray = timeLineReturn(articleJsonArray, articles);

        JSONObject pageJson = new JSONObject();
        pageJson.put("pageNum",pageInfo.getPageNum());
        pageJson.put("pageSize",pageInfo.getPageSize());
        pageJson.put("total",pageInfo.getTotal());
        pageJson.put("pages",pageInfo.getPages());
        pageJson.put("isFirstPage",pageInfo.isIsFirstPage());
        pageJson.put("isLastPage",pageInfo.isIsLastPage());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result",articleJsonArray);
        jsonObject.put("pageInfo",pageJson);
        jsonObject.put("articleNum", articleMapper.countArticle());
        jsonObject.put("showMonth", showMonth);


        return BlogJSONResult.ok(jsonObject);
    }

    @Override
    public int countArticle() {
        return articleMapper.countArticle();
    }

    @Override
    public int countArticleArchiveByArchive(String archive) {
        return articleMapper.countArticleArchiveByArchive(archive);
    }

    @Override
    public List<Article> getArticleManagement(int rows, int pageNum) {
        PageHelper.startPage(pageNum,rows);
        return articleMapper.getArticleManagement();
    }

    @Override
    public Article findArticleById(int id) {
        return articleMapper.findArticleById(id);
    }

    @Override
    public BlogJSONResult updateArticleById(Article article) {
        Article a = articleMapper.getArticleUrlById(article.getId());
        if("原创".equals(article.getArticleType())){
            article.setOriginalAuthor(article.getAuthor());
            String url = SiteOwner.SITE_OWNER_URL + "/article/details/" + a.getArticleId();
            article.setArticleUrl(url);
        }
        articleMapper.updateArticleById(article);

        Map<String, Object> dataMap = new HashMap<>(4);
        dataMap.put("articleTitle",article.getArticleTitle());
        dataMap.put("updateDate",article.getUpdateDate());
        dataMap.put("author",article.getOriginalAuthor());
        dataMap.put("articleUrl","/article/" + a.getArticleId());

        return BlogJSONResult.ok(dataMap);
    }

    @Override
    public String getArticleAuthorById(Long articleId) {
        return articleMapper.getArticleAuthorById(articleId);
    }

    @Override
    public Map<String, String> findArticleTitleByArticleId(long id) {
        Article articleInfo = articleMapper.findArticleTitleByArticleId(id);
        Map<String, String> articleMap = new HashMap<>();
        if(articleInfo != null){
            articleMap.put("articleTitle", articleInfo.getArticleTitle());
            articleMap.put("articleTabloid", articleInfo.getArticleTabloid());
        }
        return articleMap;
    }

    @Override
    public BlogJSONResult updateLikeByArticleId(long articleId) {
        articleMapper.updateLikeByArticleId(articleId);
        int likes = articleMapper.findLikesByArticleId(articleId);
        return BlogJSONResult.ok(likes);
    }

    @Override
    public int updateArticleLooks(long looks, long articleId) {
        return articleMapper.updateArticleLooks(looks, articleId);
    }

    @Override
    public BlogJSONResult deleteArticle(long id) {
        try {
            //删除本篇文章
            articleMapper.deleteByArticleId(id);
            //删除与该文章有关的所有文章点赞记录、文章评论、文章评论记录
            commentMapper.deleteCommentByArticleId(id);
            articleLikesMapper.deleteArticleLikesRecordByArticleId(id);
        }catch (Exception e){
            log.error("delete article exception,article id is [{}]", id, e);
            return BlogJSONResult.errorException("删除异常！");
        }
        return BlogJSONResult.ok();
    }

    @Override
    public List<Article> searchArticle(String searchParam,int pageNum,int rows) {
        PageHelper.startPage(pageNum,rows);
        if(StringUtil.BLANK.equals(searchParam)){
            return articleMapper.findAllArticles();
        }else {
            return articleMapper.searchArticle(searchParam);
        }
    }

    /**
     * 封装时间线中数据成JsonArray形式
     */
    private JSONArray timeLineReturn(JSONArray articleJsonArray, List<Article> articles){
        JSONObject articleJson;
        for(Article article : articles){
            String[] tagsArray = StringAndArray.stringToArray(article.getArticleTags());
            articleJson = new JSONObject();
            articleJson.put("articleId", article.getArticleId());
            articleJson.put("originalAuthor", article.getOriginalAuthor());
            articleJson.put("articleTitle", article.getArticleTitle());
            articleJson.put("articleCategories", article.getArticleCategories());
            articleJson.put("publishDate", article.getPublishDate());
            articleJson.put("articleTags", tagsArray);
            articleJsonArray.add(articleJson);
        }
        return articleJsonArray;
    }
}
