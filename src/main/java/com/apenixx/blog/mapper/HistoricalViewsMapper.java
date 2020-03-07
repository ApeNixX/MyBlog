package com.apenixx.blog.mapper;

import com.apenixx.blog.model.HistoricalViews;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author ApeNixX
 * @Date 2020/2/12 18:53
 * @Version 1.0
 * @Describe
 */
@Mapper
@Repository
public interface HistoricalViewsMapper {

    @Select("select createBy ,views from (select createBy ,views from historicalview ORDER BY createBy desc limit 6) aa order by createBy")
    List<HistoricalViews> getHistoricalViews();


    @Insert("INSERT INTO historicalview(views) VALUES (#{currentViews})")
    void writeHistoricalViews(@Param("currentViews") Integer currentViews);

}
