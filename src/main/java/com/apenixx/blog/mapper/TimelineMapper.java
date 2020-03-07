package com.apenixx.blog.mapper;

import com.apenixx.blog.model.Timeline;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author ApeNixX
 * @Date 2020/2/7 12:55
 * @Version 1.0
 * @Describe
 */
@Mapper
@Repository
public interface TimelineMapper {
    @Select("select * from timeline order by publishDate desc")
    List<Timeline> getAllTimeLine();
    @Insert("insert into timeline(content,publishDate) values (#{content},#{publishDate})")
    void save(Timeline timeline);
    @Update("update timeline set content = #{content} where id =#{id}")
    int updateDiary(Timeline timeline);
    @Delete("delete from timeline where id=#{id}")
    void deleteCategory(int id);
}
