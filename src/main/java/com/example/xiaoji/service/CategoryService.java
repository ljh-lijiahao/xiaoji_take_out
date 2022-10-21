package com.example.xiaoji.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.xiaoji.common.R;
import com.example.xiaoji.entity.Category;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface CategoryService extends IService<Category> {
    R<String> saveCategory(HttpSession session, Category category);

    R<Page<Category>> pageCategory(Integer page, Integer pageSize);

    R<String> remove(HttpSession session, Long ids);

    R<String> updateCategory(HttpSession session, Category category);

    R<List<Category>> getCategoryList(Category category);
}
