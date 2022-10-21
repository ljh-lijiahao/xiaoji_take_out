package com.example.xiaoji.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.xiaoji.common.R;
import com.example.xiaoji.dto.SetmealDto;
import com.example.xiaoji.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    R<String> saveWithDish(SetmealDto dto);

    R<Page<SetmealDto>> pageSetmeal(Integer page, Integer pageSize, String name);

    R<String> removeWithDish(List<Long> ids);

    R<String> updataStatus(Integer status, List<Long> ids);

    R<List<Setmeal>> getList(Setmeal setmeal);
}
