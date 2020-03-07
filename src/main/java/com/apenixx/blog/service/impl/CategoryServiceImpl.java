package com.apenixx.blog.service.impl;

import com.apenixx.blog.mapper.CategoryMapper;
import com.apenixx.blog.model.Categories;
import com.apenixx.blog.service.CategoryService;
import com.apenixx.blog.utils.BlogJSONResult;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author ApeNixX
 * @Date 2020/1/28 18:14
 * @Version 1.0
 * @Describe
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<String> findCategoriesName() {
        return categoryMapper.findCategoriesName();
    }

    @Override
    public List<Categories> findAllCategories(int rows, int pageNum) {
        PageHelper.startPage(pageNum,rows);
        return categoryMapper.findAllCategories();
    }

    @Override
    public BlogJSONResult addCategory(String categoryName) {
        int isExistCategory = categoryMapper.findIsExistByCategoryName(categoryName);
        if(isExistCategory == 0){
            Categories categories = new Categories();
            categories.setCategoryName(categoryName);
            categoryMapper.save(categories);
            return BlogJSONResult.ok();
        }else {
            return BlogJSONResult.errorMsg("类型已存在");
        }

    }

    @Override
    public BlogJSONResult updateCategoryById(Categories categories) {
        int isExistCategory = categoryMapper.findIsExistByCategoryName(categories.getCategoryName());
        if(isExistCategory == 0){
            categoryMapper.updateCategoryById(categories);
            return BlogJSONResult.ok();
        }else {
            return BlogJSONResult.errorMsg("类型已存在");
        }
    }

    @Override
    public BlogJSONResult deleteCategory(String categoryName) {
        categoryMapper.deleteCategory(categoryName);
        return BlogJSONResult.ok();
    }
}
