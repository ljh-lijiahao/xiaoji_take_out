package com.example.xiaoji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.xiaoji.entity.OrderDetail;
import com.example.xiaoji.mapper.OrderDetailMapper;
import com.example.xiaoji.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
