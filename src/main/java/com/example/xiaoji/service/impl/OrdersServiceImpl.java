package com.example.xiaoji.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.xiaoji.common.R;
import com.example.xiaoji.dto.OrderDto;
import com.example.xiaoji.entity.*;
import com.example.xiaoji.service.*;
import com.example.xiaoji.common.BaseContext;
import com.example.xiaoji.common.CustomException;
import com.example.xiaoji.mapper.OrdersMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     */
    @Transactional
    @Override
    public R<String> submit(Orders orders) {
        Long userId = BaseContext.getCurrentId();
        // 查询当前用户购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);
        if (shoppingCartList == null) {
            throw new CustomException("购物车为空，不能下单");
        }
        User user = userService.getById(userId);
        log.info("{}", user);
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if (addressBook == null) {
            throw new CustomException("请添加地址信息");
        }
        long orderId = IdWorker.getId();
        AtomicInteger amount = new AtomicInteger(0);
        // 向订单明细表插入数据，多条数据
        List<OrderDetail> orderDetailList = shoppingCartList.stream().map(shoppingCart -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(shoppingCart.getNumber());
            orderDetail.setDishFlavor(shoppingCart.getDishFlavor());
            orderDetail.setDishId(shoppingCart.getDishId());
            orderDetail.setSetmealId(shoppingCart.getSetmealId());
            orderDetail.setName(shoppingCart.getName());
            orderDetail.setImage(shoppingCart.getImage());
            orderDetail.setAmount(shoppingCart.getAmount());
            amount.addAndGet(shoppingCart.getAmount().multiply(new BigDecimal(shoppingCart.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());
        // 向订单表插入数据，一条数据
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        // 保存订单数据，一条数据
        log.info("{}", orders);
        save(orders);
        // 保存订单明细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetailList);
        // 清空购物车
        shoppingCartService.remove(queryWrapper);
        return R.success("下单成功");
    }

    @Override
    public R<Page<OrderDto>> userPage(Integer page, Integer pageSize) {
        LambdaQueryWrapper<Orders> ordersQueryWrapper = new LambdaQueryWrapper<>();
        ordersQueryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        ordersQueryWrapper.orderByDesc(Orders::getCheckoutTime);
        Page<Orders> ordersPage = page(new Page<>(page, pageSize), ordersQueryWrapper);
        if (ordersPage == null) {
            return R.error("暂无订单，赶快去下单吧！");
        }
        Page<OrderDto> orderDtoPage = new Page<>();
        BeanUtils.copyProperties(ordersPage, orderDtoPage, "records");
        List<Orders> records = ordersPage.getRecords();
        List<OrderDto> orderDtoList = records.stream().map(record -> {
            OrderDto orderDto = new OrderDto();
            BeanUtils.copyProperties(record, orderDto);
            LambdaQueryWrapper<OrderDetail> orderDetailQueryWrapper = new LambdaQueryWrapper<>();
            orderDetailQueryWrapper.eq(OrderDetail::getOrderId, record.getId());
            List<OrderDetail> orderDetailList = orderDetailService.list(orderDetailQueryWrapper);
            orderDto.setOrderDetails(orderDetailList);
            return orderDto;
        }).collect(Collectors.toList());
        orderDtoPage.setRecords(orderDtoList);
        return R.success(orderDtoPage);
    }

    @Override
    public R<String> again(Orders orders) {
        Long id = orders.getId();
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId, id);
        List<OrderDetail> orderDetailList = orderDetailService.list(queryWrapper);
        orderDetailList.forEach(orderDetail -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setName(orderDetail.getName());
            shoppingCart.setDishId(orderDetail.getDishId());
            shoppingCart.setSetmealId(orderDetail.getSetmealId());
            shoppingCart.setDishFlavor(orderDetail.getDishFlavor());
            shoppingCart.setNumber(orderDetail.getNumber());
            shoppingCart.setAmount(orderDetail.getAmount());
            shoppingCart.setImage(orderDetail.getImage());
            shoppingCartService.saveShoppingCart(shoppingCart);
        });
        return R.success("已添加到购物车");
    }

    @Override
    public R<Page<Orders>> pageAll(Integer page, Integer pageSize, Long number, String beginTime, String endTime) {
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        if (number != null) {
            queryWrapper.eq(Orders::getNumber, number);
        }
        if (beginTime != null && endTime != null) {
            queryWrapper.between(Orders::getCheckoutTime, beginTime, endTime);
        }
        Page<Orders> ordersPage = page(new Page<>(page, pageSize), queryWrapper);
        if (ordersPage == null) {
            return R.error("暂无订单");
        }
        return R.success(ordersPage);
    }

    @Override
    public R<String> updateOrders(Orders orders) {
        Long id = orders.getId();
        Integer status = orders.getStatus();
        if (id == null) {
            return R.error("订单错误");
        }
        LambdaUpdateWrapper<Orders> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Orders::getId, id);
        lambdaUpdateWrapper.set(Orders::getStatus, status);
        update(lambdaUpdateWrapper);
        return R.success("修改成功");
    }
}
