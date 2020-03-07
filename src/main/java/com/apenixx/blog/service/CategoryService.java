package com.apenixx.blog.service;

import com.apenixx.blog.model.Categories;
import com.apenixx.blog.utils.BlogJSONResult;

import java.util.List;

/**
 * @Author ApeNixX
 * @Date 2020/1/28 18:12
 * @Version 1.0
 * @Describe 分类业务操作
 */
public interface CategoryService {
    /**
     * 获得所有的分类
     * @return
     */
    List<String> findCategoriesName();

    /**
     * 获得分类名和对应id
     */
    List<Categories> findAllCategories(int rows, int pageNum);

    /**
     * 添加分类
     * @param categoryName 分类名
     */
    BlogJSONResult addCategory(String categoryName);
    /**
     * 更新分类
     * @param categories 分类实体
     */
    BlogJSONResult updateCategoryById(Categories categories);
    /**
     * 删除分类
     * @param categoryName 分类名
     */
    BlogJSONResult deleteCategory(String categoryName);

}
