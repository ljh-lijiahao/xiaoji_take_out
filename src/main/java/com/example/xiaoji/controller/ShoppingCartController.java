package com.example.xiaoji.controller;

import com.example.xiaoji.common.R;
import com.example.xiaoji.entity.ShoppingCart;
import com.example.xiaoji.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车
 */
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加到购物车
     */
    @PostMapping("/add")
    public R<ShoppingCart> save(@RequestBody ShoppingCart shoppingCart) {
        return shoppingCartService.saveShoppingCart(shoppingCart);
    }

    /**
     * 查看购物车数据
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> getList() {
        return shoppingCartService.getList();
    }

    /**
     * 更改购物车某物品数量
     */
    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart) {
        return shoppingCartService.sub(shoppingCart);
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clean")
    public R<String> clean() {
        return shoppingCartService.clean();
    }
}
