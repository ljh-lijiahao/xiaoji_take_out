package com.example.xiaoji.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.xiaoji.dto.OrderDto;
import com.example.xiaoji.entity.Orders;
import com.example.xiaoji.service.OrderDetailService;
import com.example.xiaoji.service.OrdersService;
import com.example.xiaoji.common.R;
import com.example.xiaoji.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrdersService ordersService;
    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private ShoppingCartService shoppingCartService;

    /**
     * 用户下单
     */
    @Transactional
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        return ordersService.submit(orders);
    }

    /**
     * 个人用户订单分页查询
     */
    @GetMapping("/userPage")
    public R<Page<OrderDto>> userPage(Integer page, Integer pageSize) {
        return ordersService.userPage(page,pageSize);
    }

    /**
     * 再来一单
     */
    @PostMapping("/again")
    public R<String> again(@RequestBody Orders orders) {
        return ordersService.again(orders);
    }

    /**
     * 所有用户订单分页查询
     */
    @GetMapping("/page")
    public R<Page<Orders>> page(Integer page, Integer pageSize, Long number, String beginTime, String endTime) {
        return ordersService.pageAll(page,pageSize,number,beginTime,endTime);
    }

    /**
     * 修改订单状态
     */
    @PutMapping
    public R<String> update(@RequestBody Orders orders) {
        return ordersService.updateOrders(orders);
    }

}
