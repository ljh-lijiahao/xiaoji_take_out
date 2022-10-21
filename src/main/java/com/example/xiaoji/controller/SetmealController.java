package com.example.xiaoji.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.xiaoji.common.R;
import com.example.xiaoji.dto.SetmealDto;
import com.example.xiaoji.entity.Setmeal;
import com.example.xiaoji.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    /**
     * 添加套餐
     */
    @PostMapping
    @CacheEvict(value = "setmeal", allEntries = true)
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        return setmealService.saveWithDish(setmealDto);
    }

    /**
     * 套餐分页查询
     */
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(Integer page, Integer pageSize, String name) {
        return setmealService.pageSetmeal(page,pageSize,name);
    }

    /**
     * 删除套餐
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        return setmealService.removeWithDish(ids);
    }

    /**
     * 根据id设置套餐停售起售状态
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Integer status, @RequestParam List<Long> ids) {
        return setmealService.updataStatus(status,ids);
    }

    /**
     * 根据条件查询套餐数据
     */
    @GetMapping("/list")
    public R<List<Setmeal>> getList(Setmeal setmeal) {
        return setmealService.getList(setmeal);
    }

}
