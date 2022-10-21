package com.example.xiaoji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.xiaoji.service.DishFlavorService;
import com.example.xiaoji.entity.DishFlavor;
import com.example.xiaoji.mapper.DishFlavorMapper;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
