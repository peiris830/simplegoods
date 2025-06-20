package com.simplegoods.simplegoods.service;

import com.simplegoods.simplegoods.model.CartItem;

import java.util.List;

public interface CartService {
    List<CartItem> getCartItems(Long userId);
    CartItem addToCart(Long userId, Long productId, Integer quantity);
    CartItem updateCartItem(Long userId, Long productId, Integer quantity);
    void removeFromCart(Long userId, Long productId);
}
