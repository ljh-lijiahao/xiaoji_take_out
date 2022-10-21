package com.example.xiaoji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.xiaoji.common.R;
import com.example.xiaoji.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService extends IService<ShoppingCart> {
    R<ShoppingCart> saveShoppingCart(ShoppingCart shoppingCart);

    R<List<ShoppingCart>> getList();

    R<String> sub(ShoppingCart shoppingCart);

    R<String> clean();
}
