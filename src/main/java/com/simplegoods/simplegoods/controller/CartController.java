package com.simplegoods.simplegoods.controller;

import com.simplegoods.simplegoods.service.CartService;
import com.simplegoods.simplegoods.model.CartItem;
import com.simplegoods.simplegoods.model.User;
import com.simplegoods.simplegoods.repository.UserRepository;
import com.simplegoods.simplegoods.exception.ResourceNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final UserRepository userRepository;

    public CartController(CartService cartService, UserRepository userRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    private User getUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @GetMapping
    public ResponseEntity<List<CartItem>> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        return ResponseEntity.ok(cartService.getCartItems(user.getId()));
    }

    @PostMapping
    public ResponseEntity<CartItem> addToCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") @Positive(message = "Quantity must be > 0") int quantity) {

        User user = getUser(userDetails);
        return ResponseEntity.ok(
                cartService.addToCart(user.getId(), productId, quantity));
    }

    @PutMapping
    public ResponseEntity<CartItem> updateCartItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long productId,
            @RequestParam @Positive(message = "Quantity must be > 0") int quantity) {

        User user = getUser(userDetails);
        return ResponseEntity.ok(
                cartService.updateCartItem(user.getId(), productId, quantity));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeFromCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId) {

        User user = getUser(userDetails);
        cartService.removeFromCart(user.getId(), productId);
        return ResponseEntity.noContent().build();
    }
}