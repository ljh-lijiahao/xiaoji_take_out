package com.example.xiaoji.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.xiaoji.common.R;
import com.example.xiaoji.dto.DishDto;
import com.example.xiaoji.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {

    R<String> saveWithFlavor(DishDto dishDto);

    R<Page<DishDto>> pageDish(Integer page, Integer pageSize, String name);

    R<DishDto> getByIdWithFlavor(Long id);

    R<String> updateWithFlavor(DishDto dishDto);

    R<String> updataStatus(Integer status, List<Long> ids);

    R<String> removeDish(List<Long> ids);

    R<List<DishDto>> getList(Dish dish);
}
