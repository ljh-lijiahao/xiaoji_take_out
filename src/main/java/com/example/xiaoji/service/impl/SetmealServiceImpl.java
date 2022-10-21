package com.example.xiaoji.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.xiaoji.common.CustomException;
import com.example.xiaoji.common.R;
import com.example.xiaoji.dto.SetmealDto;
import com.example.xiaoji.entity.SetmealDish;
import com.example.xiaoji.mapper.SetmealMapper;
import com.example.xiaoji.service.SetmealDishService;
import com.example.xiaoji.entity.Setmeal;
import com.example.xiaoji.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 添加套餐信息，并保存对应的套餐菜品数据
     */
    @Override
    public R<String> saveWithDish(@RequestBody SetmealDto setmealDto) {
        save(setmealDto);
        List<SetmealDish> setmealDishList = setmealDto.getSetmealDishes();
        setmealDishList.forEach((setmealDish) -> setmealDish.setSetmealId(setmealDto.getId()));
        setmealDishService.saveBatch(setmealDishList);
        return R.success("添加成功");
    }

    @Override
    public R<Page<SetmealDto>> pageSetmeal(Integer page, Integer pageSize, String name) {
        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
        setmealQueryWrapper.like(StringUtils.isNotEmpty(name), Setmeal::getName, name);
        setmealQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        Page<Setmeal> setmealPage = page(new Page<>(page, pageSize), setmealQueryWrapper);

        Page<SetmealDto> setmealDtoPage = new Page<>();
        BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records");
        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto> setmealDtoList = records.stream().map(record -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(record, setmealDto);
            setmealDto.setCategoryName(getById(record.getId()).getName());
            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(setmealDtoList);
        return R.success(setmealDtoPage);
    }

    /**
     * 删除套餐信息，并删除对应的套餐菜品数据
     */
    @Transactional
    @CacheEvict(value = "setmeal", allEntries = true)
    @Override
    public R<String> removeWithDish(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> setmealqueryWrapper = new LambdaQueryWrapper<>();
        setmealqueryWrapper.in(Setmeal::getId, ids).eq(Setmeal::getStatus, 1);
        if (this.count(setmealqueryWrapper) > 0) {
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        removeByIds(ids);
        LambdaQueryWrapper<SetmealDish> setmealDishQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishQueryWrapper.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(setmealDishQueryWrapper);
        return R.success("删除成功");
    }

    @Transactional
    @Override
    public R<String> updataStatus(Integer status, List<Long> ids) {
        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Setmeal::getId, ids);
        updateWrapper.set(Setmeal::getStatus, status);
        update(updateWrapper);
        return R.success("状态设置成功");
    }

    @Cacheable(value = "setmeal", key = "#setmeal.categoryId+'_'+#setmeal.status")
    @Override
    public R<List<Setmeal>> getList(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        List<Setmeal> setmealList = list(queryWrapper);
        return R.success(setmealList);
    }
}