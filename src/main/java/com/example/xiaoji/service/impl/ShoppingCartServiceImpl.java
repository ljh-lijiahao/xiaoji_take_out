package com.example.xiaoji.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.xiaoji.common.BaseContext;
import com.example.xiaoji.common.R;
import com.example.xiaoji.entity.ShoppingCart;
import com.example.xiaoji.service.ShoppingCartService;
import com.example.xiaoji.mapper.ShoppingCartMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
    @Transactional
    @Override
    public R<ShoppingCart> saveShoppingCart(ShoppingCart shoppingCart) {
        shoppingCart.setUserId(BaseContext.getCurrentId());
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, shoppingCart.getUserId());
        queryWrapper.eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId());
        //queryWrapper.eq(shoppingCart.getDishFlavor() != null, ShoppingCart::getDishFlavor, shoppingCart.getDishFlavor());
        queryWrapper.eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        ShoppingCart shoppingCartOne = getOne(queryWrapper);
        if (shoppingCartOne != null) {
            shoppingCartOne.setNumber(shoppingCartOne.getNumber() + 1);
            updateById(shoppingCartOne);
        } else {
            shoppingCart.setCreateTime(LocalDateTime.now());
            save(shoppingCart);
            shoppingCartOne = shoppingCart;
        }
        return R.success(shoppingCartOne);
    }

    @Override
    public R<List<ShoppingCart>> getList() {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCartList = list(queryWrapper);
        return R.success(shoppingCartList);
    }

    @Override
    public R<String> sub(ShoppingCart shoppingCart) {
        LambdaUpdateWrapper<ShoppingCart> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        updateWrapper.eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId());
        updateWrapper.eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        //updateWrapper.eq(shoppingCart.getDishFlavor() != null, ShoppingCart::getDishFlavor, shoppingCart.getDishFlavor());
        ShoppingCart shoppingCartOne = getOne(updateWrapper);
        if (shoppingCartOne.getNumber() == 1) {
            remove(updateWrapper);
        } else {
            updateWrapper.set(ShoppingCart::getNumber, shoppingCartOne.getNumber() - 1);
            update(updateWrapper);
        }
        return R.success("更改成功");
    }

    @Override
    public R<String> clean() {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        if (getList() != null) {
            queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
            remove(queryWrapper);
        }
        return R.success("购物车已清空");
    }
}
