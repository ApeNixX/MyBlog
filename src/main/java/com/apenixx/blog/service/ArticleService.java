package com.apenixx.blog.service;

import com.apenixx.blog.model.Article;
import com.apenixx.blog.utils.BlogJSONResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Author ApeNixX
 * @Date 2020/1/20 14:26
 * @Version 1.0
 * @Describe
 */
public interface ArticleService {
    /**
     * 分页获得所有文章
     * @param rows 一页显示文章数
     * @param pageNo 第几页
     * @return 该页所有文章
     */
    List<Article> findAllArticles(Integer rows, Integer pageNo);
    /**
     * 获得文章
     * @param articleId 文章id
     * @return
     */
    Article getArticlesByArticleId(long articleId, String username);
    /**
     * 保存文章
     * @param article 文章
     * @return  status: 200--成功   500--失败
     */
    void insertArticle(Article article);


    /**
     * 分页获得该分类下的所有文章
     * @param category 分类名
     * @param rows 一页大小
     * @param pageNum 页数
     * @return
     */
    List<Article> findArticleByCategory(String category, int rows, int pageNum);

    /**
     * 通过标签分页获得文章部分信息
     * @param tag
     * @return
     */
    List<Article> findArticleByTag(String tag, int rows, int pageNum);

    /**
     * 分页获得该归档日期下的所有文章
     * @param archive 归档日期
     * @param rows 一页大小
     * @param pageNum 页数
     * @return
     */
    BlogJSONResult findArticleByArchive(String archive, int rows, int pageNum);

    /**
     * 计算所有文章的数量
     * @return 所有文章的数量
     */
    int countArticle();

    /**
     * 计算该归档日期文章的数目
     * @param archive 归档日期
     * @return 该归档日期下文章的数目
     */
    int countArticleArchiveByArchive(String archive);

    /**
     * 分页获得文章管理
     */
    List<Article> getArticleManagement(int rows, int pageNum);
    /**
     * 通过id获取文章
     */
    Article findArticleById(int id);


    /**
     * 修改文章
     * @return
     */
    @Transactional
    BlogJSONResult updateArticleById(Article article);

    /**
     * 获取文章作者
     * @return
     */
    String getArticleAuthorById(Long articleId);

    /**
     * 通过文章id获得文章名和文章摘要
     * @param id 文章id
     * @return 文章名
     */
    Map<String, String> findArticleTitleByArticleId(long id);

    /**
     * 文章点赞
     * @param articleId 文章id
     * @return 目前点赞数
     */
    BlogJSONResult updateLikeByArticleId(long articleId);

    /**
     * 更新文章浏览量
     * @param articleId 文章id
     * @return 更新条数
     */
    int updateArticleLooks(long looks,long articleId);


    /**
     * 通过id删除文章
     * @param id 文章id
     */
    @Transactional
    BlogJSONResult deleteArticle(long id);

    /**
     * 搜索文章
     * @param searchParam 文章搜索参数
     */
    List<Article> searchArticle(String searchParam,int pageNum,int rows);

}
