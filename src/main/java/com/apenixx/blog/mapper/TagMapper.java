package com.apenixx.blog.mapper;

import com.apenixx.blog.model.Tag;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author ApeNixX
 * @Date 2020/1/29 17:07
 * @Version 1.0
 * @Describe
 */
@Mapper
@Repository
public interface TagMapper {
    @Insert("insert into tags(tagName,tagSize) values(#{tagName},#{tagSize})")
    void save(Tag tag);

    @Select("select * from tags order by id desc")
    List<Tag> findTagsCloud();

    @Select("select count(*) from tags")
    int countTagsNum();

    @Select("select IFNULL(max(id),0) from tags where tagName = #{tagName}")
    int findIsExistByTagName(@Param("tagName") String tagName);

    @Update("update tags set tagName=#{tagName} where id =#{id}")
    int updateTags(Tag tag);

    @Delete("delete from tags where id=#{id}")
    int deleteTags(@Param("id")int id);
}
