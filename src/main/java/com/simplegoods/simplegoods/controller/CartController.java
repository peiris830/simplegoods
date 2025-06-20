package com.simplegoods.simplegoods.controller;

import com.simplegoods.simplegoods.service.CartService;
import com.simplegoods.simplegoods.model.CartItem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItem>> getCartItems(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCartItems(userId));
    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<CartItem> addToCart(@PathVariable Long userId, @RequestParam Long productId, @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.addToCart(userId, productId, quantity));
    }

    @PutMapping("/{userId}/update")
    public ResponseEntity<CartItem> updateCartItem(@PathVariable Long userId, @RequestParam Long productId, @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.updateCartItem(userId, productId, quantity));
    }

    @DeleteMapping("/{userId}/remove")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long userId, @RequestParam Long productId) {
        cartService.removeFromCart(userId, productId);
        return ResponseEntity.noContent().build();
    }
}
