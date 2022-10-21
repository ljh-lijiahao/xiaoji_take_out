package com.example.xiaoji.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.xiaoji.common.R;
import com.example.xiaoji.entity.Category;
import com.example.xiaoji.mapper.CategoryMapper;
import com.example.xiaoji.common.CustomException;
import com.example.xiaoji.entity.Dish;
import com.example.xiaoji.entity.Setmeal;
import com.example.xiaoji.service.CategoryService;
import com.example.xiaoji.service.DishService;
import com.example.xiaoji.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Resource
    private DishService dishService;
    @Resource
    private SetmealService setmealService;

    @Override
    public R<String> saveCategory(HttpSession session, Category category) {
        save(category);
        log.info("用户 {} 添加了 {} 分类", session.getAttribute("employee"), category.getName());
        return R.success("分类添加成功");
    }

    @Override
    public R<Page<Category>> pageCategory(Integer page, Integer pageSize) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);
        Page<Category> categoryPage = page(new Page<>(page, pageSize), queryWrapper);
        return R.success(categoryPage);
    }

    /**
     * 根据id删除分类，状态为0才能删除
     */
    @Override
    public R<String> remove(HttpSession session,Long id) {
        Category category = getById(id);
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
        dishQueryWrapper.eq(Dish::getCategoryId, id);
        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
        setmealQueryWrapper.eq(Setmeal::getCategoryId, id);
        if (dishService.count(dishQueryWrapper) > 0 || setmealService.count(setmealQueryWrapper) > 0) {
            throw new CustomException("当前分类下关联了菜品或套餐，不能删除");
        }
        removeById(id);
        log.info("用户 {} 删除了 {} 分类", session.getAttribute("employee"), category.getName());
        return R.success("分类删除成功");
    }

    @Override
    public R<String> updateCategory(HttpSession session, Category category) {
        updateById(category);
        log.info("用户 {} 修改了 {} 分类", session.getAttribute("employee"), category.getName());
        return R.success("分类信息修改成功");
    }

    @Override
    public R<List<Category>> getCategoryList(Category category) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> categoryList = list(queryWrapper);
        return R.success(categoryList);
    }
}
