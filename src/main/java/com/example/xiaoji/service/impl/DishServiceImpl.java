package com.example.xiaoji.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.xiaoji.common.CustomException;
import com.example.xiaoji.common.R;
import com.example.xiaoji.dto.DishDto;
import com.example.xiaoji.mapper.DishMapper;
import com.example.xiaoji.service.CategoryService;
import com.example.xiaoji.service.DishFlavorService;
import com.example.xiaoji.entity.Dish;
import com.example.xiaoji.entity.DishFlavor;
import com.example.xiaoji.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 添加菜品信息，并保存对应的口味数据
     */
    @Transactional
    @Override
    public R<String> saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);
        List<DishFlavor> flavorList = dishDto.getFlavors();
        flavorList.forEach((flavor) -> {
            flavor.setDishId(dishDto.getId());
            if ("忌口".equals(flavor.getName())) {
                flavor.setValue(flavor.getValue().substring(0, flavor.getValue().length() - 1) + ",\"无\"]");
            }
        });
        stringRedisTemplate.delete("dish_" + dishDto.getCategoryId() + "*");
        return R.success("添加成功");
    }

    @Override
    public R<Page<DishDto>> pageDish(Integer page, Integer pageSize, String name) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        Page<Dish> dishPage = page(new Page<>(page, pageSize), queryWrapper);

        Page<DishDto> dishDtoPage = new Page<>();
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");
        List<Dish> records = dishPage.getRecords();
        List<DishDto> dishDtoList = records.stream().map(record -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(record, dishDto);
            dishDto.setCategoryName(categoryService.getById(record.getCategoryId()).getName());
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(dishDtoList);
        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     */
    @Override
    public R<DishDto> getByIdWithFlavor(Long id) {
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, id);
        dishDto.setFlavors(dishFlavorService.list(queryWrapper));
        return R.success(dishDto);
    }

    /**
     * 修改菜品信息，并保存对应的口味数据
     */
    @Transactional
    @Override
    public R<String> updateWithFlavor(DishDto dishDto) {
        updateById(dishDto);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        List<DishFlavor> flavorList = dishDto.getFlavors();
        flavorList.forEach((flavor) -> flavor.setDishId(dishDto.getId()));
        dishFlavorService.saveBatch(flavorList);
        stringRedisTemplate.delete("dish_" + dishDto.getCategoryId() + "*");
        return R.success("修改成功");
    }

    @Override
    public R<String> updataStatus(Integer status, List<Long> ids) {
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Dish::getId, ids);
        updateWrapper.set(Dish::getStatus, status);
        update(updateWrapper);
        return R.success("状态设置成功");
    }

    @Override
    public R<String> removeDish(List<Long> ids) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId, ids).eq(Dish::getStatus, 1);
        if (count(queryWrapper) > 0) {
            throw new CustomException("此菜品正在售出中，不能删除");
        }
        removeByIds(ids);
        return R.success("删除成功");
    }

    @Override
    public R<List<DishDto>> getList(Dish dish) {
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        String jsonString = stringRedisTemplate.opsForValue().get(key);
        List<DishDto> dishDtoList = JSON.parseArray(jsonString, DishDto.class);

        if (dishDtoList != null) {
            return R.success(dishDtoList);
        }
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
        dishQueryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        dishQueryWrapper.eq(Dish::getStatus, 1);
        dishQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishList = list(dishQueryWrapper);

        dishDtoList = dishList.stream().map(item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            dishDto.setCategoryName(categoryService.getById(item.getCategoryId()).getName());
            LambdaQueryWrapper<DishFlavor> dishFlavorQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorQueryWrapper.eq(DishFlavor::getDishId, item.getId());
            List<DishFlavor> dishFlavorList = dishFlavorService.list(dishFlavorQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());
        String toJSONString = JSON.toJSONString(dishDtoList);
        stringRedisTemplate.opsForValue().set(key, toJSONString, 60, TimeUnit.MINUTES);
        return R.success(dishDtoList);
    }
}
