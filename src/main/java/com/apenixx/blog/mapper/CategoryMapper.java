package com.apenixx.blog.mapper;

import com.apenixx.blog.model.Categories;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author ApeNixX
 * @Date 2020/1/28 18:10
 * @Version 1.0
 * @Describe
 */
@Mapper
@Repository
public interface CategoryMapper {
    @Select("select categoryName from categories")
    List<String> findCategoriesName();
    @Select("select id,categoryName from categories")
    List<Categories> findAllCategories();
    @Insert("insert into categories(categoryName) value(#{categoryName})")
    void save(Categories categories);
    @Select("select IFNULL(max(id),0) from categories where categoryName = #{categoryName}")
    int findIsExistByCategoryName(@Param("categoryName")String categoryName);
    @Delete("delete from categories where categoryName=#{categoryName}")
    void deleteCategory(String categoryName);
    @Update("update categories set categoryName=#{categoryName} where id=#{id}")
    void updateCategoryById(Categories categories);
}
