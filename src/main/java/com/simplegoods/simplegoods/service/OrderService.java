package com.simplegoods.simplegoods.service;

import com.simplegoods.simplegoods.model.Order;
import java.util.List;

public interface OrderService {
    Order placeOrder(Long userId);

    List<Order> getUserOrders(Long userId);

    Order getOrderById(Long orderId, Long userId);
}