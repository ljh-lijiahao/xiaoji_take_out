package com.example.xiaoji.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.xiaoji.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
