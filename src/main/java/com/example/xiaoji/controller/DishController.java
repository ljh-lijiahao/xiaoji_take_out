package com.example.xiaoji.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.xiaoji.common.R;
import com.example.xiaoji.dto.DishDto;
import com.example.xiaoji.entity.Dish;
import com.example.xiaoji.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("dish")
public class DishController {
    @Autowired
    private DishService dishService;

    /**
     * 添加菜品
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        return dishService.saveWithFlavor(dishDto);
    }

    /**
     * 菜品分页查询
     */
    @GetMapping("/page")
    public R<Page<DishDto>> page(Integer page, Integer pageSize, String name) {
        return dishService.pageDish(page, pageSize, name);
    }

    /**
     * 根据id查询菜品信息
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id) {
        return dishService.getByIdWithFlavor((id));
    }

    /**
     * 修改菜品信息
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        return dishService.updateWithFlavor(dishDto);
    }

    /**
     * 根据id设置菜品停售起售状态
     */
    @Transactional
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Integer status, @RequestParam List<Long> ids) {
        return dishService.updataStatus(status, ids);
    }

    /**
     * 根据id设置删除菜品
     */
    @Transactional
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        return dishService.removeDish(ids);
    }

    /**
     * 根据菜品分类查询菜品数据及口味信息
     */
    @GetMapping("/list")
    public R<List<DishDto>> getList(Dish dish) {
        return dishService.getList(dish);
    }
}
