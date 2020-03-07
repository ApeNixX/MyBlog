package com.apenixx.blog.mapper;

import com.apenixx.blog.model.Article;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author ApeNixX
 * @Date 2020/1/20 14:11
 * @Version 1.0
 * @Describe
 */

@Mapper
@Repository
public interface ArticleMapper {
    @Select("select articleId,looks,articleCover,originalAuthor,articleTags,articleTitle,articleType,publishDate,originalAuthor,articleCategories,articleTabloid,likes from article order by id desc")
    List<Article> findAllArticles();

    @Update("update article set likes=likes+1 where articleId=#{articleId}")
    void updateLikeByArticleId(@Param("articleId") long articleId);

    @Select("select IFNULL(max(likes),0) from article where articleId=#{articleId}")
    int findLikesByArticleId(@Param("articleId") long articleId);

    @Select("select * from article where articleId=#{articleId}")
    Article getArticleByArticleId(@Param("articleId") long articleId);

    @Insert("insert into article(articleId,author,originalAuthor,articleTitle,publishDate,updateDate,articleContent,articleTags,articleType,articleCategories,articleUrl,articleTabloid,likes,lastArticleId,nextArticleId,articleCover) " +
            "values(#{articleId},#{author},#{originalAuthor},#{articleTitle},#{publishDate},#{updateDate},#{articleContent},#{articleTags},#{articleType},#{articleCategories},#{articleUrl},#{articleTabloid},#{likes},#{lastArticleId},#{nextArticleId},#{articleCover})")
    void save(Article article);

    @Select("select articleId from article order by id desc limit 1")
    Article findEndArticleId();

    @Select("select articleId,looks,articleCover,originalAuthor,articleTags,articleTitle,articleType,publishDate,originalAuthor,articleCategories,articleTabloid,likes from article where articleCategories=#{category} order by id desc")
    List<Article> findArticleByCategory(@Param("category") String category);

    @Select("select articleId,looks,articleCover,originalAuthor,articleTags,articleTitle,articleType,publishDate,originalAuthor,articleCategories,articleTabloid,likes from article where articleTags like '%${tag}%' order by id desc")
    List<Article> findArticleByTag(@Param("tag") String tag);

    @Select("select articleId,originalAuthor,articleTitle,articleTags,articleType,articleCategories,publishDate from article where publishDate like '%${archive}%' order by id desc")
    List<Article> findArticleByArchive(@Param("archive") String archive);

    @Select("select count(*) from article")
    int countArticle();

    @Select("select count(*) from article where publishDate like '%${archive}%'")
    int countArticleArchiveByArchive(@Param("archive") String archive);

    @Select("select id,articleId,articleCover,looks,likes,originalAuthor,articleTitle,articleCategories,publishDate from article order by id desc")
    List<Article> getArticleManagement();


    @Delete("delete from article where articleId=#{articleId}")
    void deleteByArticleId(long articleId);

    @Select("select articleId, lastArticleId, nextArticleId from article where id=#{id}")
    Article findAllArticleId(long id);

    @Select("select id,articleId,articleCover,originalAuthor,articleTitle,articleContent,articleCategories,articleTags,articleType,articleUrl from article where id=#{id}")
    Article findArticleById(int id);

    @Update("update article set articleCover=#{articleCover},originalAuthor=#{originalAuthor},articleTitle=#{articleTitle},updateDate=#{updateDate},articleContent=#{articleContent},articleTags=#{articleTags},articleType=#{articleType},articleCategories=#{articleCategories},articleUrl=#{articleUrl} where id=#{id}")
    void updateArticleById(Article article);

    @Select("select articleId from article where id=#{id}")
    Article getArticleUrlById(int id);

    @Select("select author from article where articleId=#{articleId}")
    String getArticleAuthorById(@Param("articleId") long articleId);

    @Select("select articleTitle,articleTabloid from article where articleId=#{articleId}")
    Article findArticleTitleByArticleId(@Param("articleId") long articleId);

    @Update("update article set looks=#{looks} where  articleId=#{articleId}")
    int updateArticleLooks(@Param("looks")long looks,@Param("articleId")long articleId);

    @Select("select * from article order by looks desc limit 8")
    List<Article> getArticleByLooks();

    @Select("select * from article order by looks desc limit 3")
    List<Article> getIndexArticleByLooks();

    @Select("select * from article order by RAND() limit 4")
    List<Article> getArticleByRand();

    @Select("select * from article order by publishDate desc limit 8")
    List<Article> getArticleByTime();

    @Select("select articleId,looks,articleCover,originalAuthor,articleTags,articleTitle,articleType,publishDate,originalAuthor,articleCategories,articleTabloid,likes from article where articleTitle like '%${searchParam}%' or articleCategories like '%${searchParam}%' or articleTags like '%${searchParam}%' or articleContent like '%${searchParam}%'")
    List<Article> searchArticle(@Param("searchParam") String searchParam);
}
