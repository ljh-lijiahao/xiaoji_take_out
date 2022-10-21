package com.example.xiaoji.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.xiaoji.common.R;
import com.example.xiaoji.entity.Category;
import com.example.xiaoji.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 分类管理
 */
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 添加分类
     */
    @PostMapping
    public R<String> save(HttpSession session, @RequestBody Category category) {
        return categoryService.saveCategory(session,category);
    }

    /**
     * 分类分页查询
     */
    @GetMapping("/page")
    public R<Page<Category>> page(Integer page, Integer pageSize) {
        return categoryService.pageCategory(page,pageSize);
    }

    /**
     * 根据id删除分类
     */
    @DeleteMapping
    public R<String> delete(HttpSession session, Long ids) {
        return categoryService.remove(session,ids);
    }

    /**
     * 根据id修改分类信息
     */
    @PutMapping
    public R<String> update(HttpSession session, @RequestBody Category category) {
        return categoryService.updateCategory(session,category);
    }

    /**
     * 根据条件查询分类数据
     */
    @GetMapping("/list")
    public R<List<Category>> getCategoryList(Category category) {
        return categoryService.getCategoryList(category);
    }

}
