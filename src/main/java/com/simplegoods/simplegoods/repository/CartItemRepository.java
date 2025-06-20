package com.simplegoods.simplegoods.repository;

import com.simplegoods.simplegoods.model.CartItem;
import com.simplegoods.simplegoods.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(User user);

    CartItem findByUserAndProductId(User user, Long productId);

    void deleteByUserAndProductId(User user, Long productId);
}
