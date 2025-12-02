package com.simplegoods.simplegoods.repository;

import com.simplegoods.simplegoods.model.Order;
import com.simplegoods.simplegoods.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserOrderByCreatedAtDesc(User user);
}