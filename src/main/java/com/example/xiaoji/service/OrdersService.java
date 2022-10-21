package com.example.xiaoji.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.xiaoji.common.R;
import com.example.xiaoji.dto.OrderDto;
import com.example.xiaoji.entity.Orders;

public interface OrdersService extends IService<Orders> {
    R<String> submit(Orders orders);

    R<Page<OrderDto>> userPage(Integer page, Integer pageSize);

    R<String> again(Orders orders);

    R<Page<Orders>> pageAll(Integer page, Integer pageSize, Long number, String beginTime, String endTime);

    R<String> updateOrders(Orders orders);
}
