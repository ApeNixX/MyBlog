package com.apenixx.blog.mapper;

import com.apenixx.blog.model.Comment;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.StatementType;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author ApeNixX
 * @Date 2020/2/4 13:45
 * @Version 1.0
 * @Describe 评论sql
 */
@Repository
@Mapper
public interface CommentMapper {
    //生成和得到主键
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, statementType = StatementType.STATEMENT,resultType=int.class)
    @Insert("insert into comment_record(articleId,pId,answererId,respondentId,commentDate,likes,commentContent,isRead,location)" +
            " values(#{articleId},#{pId},#{answererId},#{respondentId},#{commentDate},#{likes},#{commentContent},#{isRead},#{location})")
    int save(Comment comment);


    @Select("select * from comment_record where articleId=#{articleId} and pId=#{pId} order by id desc")
    List<Comment> findCommentByArticleIdAndPid(@Param("articleId") long articleId, @Param("pId") long pId);

    @Select("select * from comment_record where articleId=#{articleId} and pId=#{pId}")
    List<Comment> findCommentByArticleIdAndPidNoOrder(@Param("articleId") long articleId, @Param("pId") long pId);

    @Select("select id,pId,articleId,answererId,commentDate,isRead from comment_record where respondentId=#{respondentId} and answererId<>#{respondentId} order by id desc")
    List<Comment> getUserCommentByRespondentId(@Param("respondentId") int respondentId);

    @Select("select count(*) from comment_record where isRead=1 and respondentId=#{respondentId} and answererId<>#{respondentId}")
    int countIsReadNumByRespondentId(@Param("respondentId") int respondentId);

    @Update("update comment_record set isRead=0 where id=#{id}")
    void readCommentRecordById(int id);

    @Update("update comment_record set isRead=0 where respondentId=#{respondentId}")
    void readCommentRecordByRespondentId(int respondentId);

    @Select("select count(*) from comment_record where articleId=#{articleId}")
    int countNumByArticleId(@Param("articleId") long articleId);

    @Select("select count(*) from comment_record")
    int countTotalCommentNum();

    @Select("select id,articleId,pId,answererId,respondentId,commentDate,commentContent from comment_record order by commentDate desc limit 5")
    List<Comment> findFiveNewComment();

    @Select("select * from comment_record")
    List<Comment> getAllComment();

    @Delete("delete from comment_record where id=#{id}")
    int deleteComment(@Param("id")int id);

    @Delete("delete from comment_record where articleId=#{articleId}")
    void deleteCommentByArticleId(long articleId);
}
