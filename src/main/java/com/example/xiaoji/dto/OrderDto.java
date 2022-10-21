package com.example.xiaoji.dto;

import com.example.xiaoji.entity.OrderDetail;
import com.example.xiaoji.entity.Orders;
import lombok.Data;

import java.util.List;

@Data
public class OrderDto extends Orders {
    private List<OrderDetail> orderDetails;

    private Integer sumNum;
}
