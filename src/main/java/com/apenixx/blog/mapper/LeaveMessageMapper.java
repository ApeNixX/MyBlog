package com.apenixx.blog.mapper;

import com.apenixx.blog.model.LeaveMessage;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author ApeNixX
 * @Date 2020/2/5 13:21
 * @Version 1.0
 * @Describe  留言sql
 */
@Mapper
@Repository
public interface LeaveMessageMapper {
    @Insert("insert into leave_message_record(pageName,pId,answererId,respondentId,leaveMessageDate,likes,leaveMessageContent,isRead,location) " +
            "values(#{pageName},#{pId},#{answererId},#{respondentId},#{leaveMessageDate},#{likes},#{leaveMessageContent},#{isRead},#{location})")
    void save(LeaveMessage leaveMessage);

    @Select("select * from leave_message_record where pageName=#{pageName} and pId=#{pId} order by id desc")
    List<LeaveMessage> findAllLeaveMessage(@Param("pageName") String pageName, @Param("pId") int pId);

    @Select("select id,answererId,respondentId,leaveMessageDate,leaveMessageContent,location from leave_message_record where pageName=#{pageName} and pId=#{pId}")
    List<LeaveMessage> findLeaveMessageReplyByPageNameAndPid(@Param("pageName") String pageName, @Param("pId") int pId);

    @Select("select id,pId,pageName,answererId,leaveMessageDate,isRead from leave_message_record where respondentId=#{respondentId} and answererId<>#{respondentId} order by id desc")
    List<LeaveMessage> getUserLeaveMessage(@Param("respondentId") int respondentId);


    @Select("select count(*) from leave_message_record where isRead=1 and respondentId=#{respondentId} and answererId<>#{respondentId}")
    int countIsReadNumByRespondentId(@Param("respondentId") int respondentId);

    @Update("update leave_message_record set isRead=0 where id=#{id}")
    void readOneLeaveMessageRecord(int id);

    @Update("update leave_message_record set isRead=0 where respondentId=#{respondentId}")
    void readLeaveMessageRecordByRespondentId(int respondentId);

    @Select("select count(*) from leave_message_record")
    int countTotalMsgNum();

    @Select("select * from leave_message_record")
    List<LeaveMessage> getAllMsg();

    @Delete("delete from leave_message_record where id=#{id}")
    int deleteMsg(@Param("id")int id);
}
