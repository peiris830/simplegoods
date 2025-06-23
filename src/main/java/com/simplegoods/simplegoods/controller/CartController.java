package com.simplegoods.simplegoods.controller;

import com.simplegoods.simplegoods.service.CartService;
import com.simplegoods.simplegoods.model.CartItem;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItem>> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCartItems(userId));
    }

    @PostMapping("/{userId}")
    public ResponseEntity<CartItem> addToCart(
            @PathVariable Long userId,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1")
            @Positive(message = "Quantity must be > 0") int quantity) {

        return ResponseEntity.ok(
                cartService.addToCart(userId, productId, quantity)
        );
    }

    @PutMapping("/{userId}")
    public ResponseEntity<CartItem> updateCartItem(
            @PathVariable Long userId,
            @RequestParam Long productId,
            @RequestParam
            @Positive(message = "Quantity must be > 0") int quantity) {

        return ResponseEntity.ok(
                cartService.updateCartItem(userId, productId, quantity)
        );
    }

    @DeleteMapping("/{userId}/{productId}")
    public ResponseEntity<Void> removeFromCart(
            @PathVariable Long userId,
            @PathVariable Long productId) {

        cartService.removeFromCart(userId, productId);
        return ResponseEntity.noContent().build();
    }
}