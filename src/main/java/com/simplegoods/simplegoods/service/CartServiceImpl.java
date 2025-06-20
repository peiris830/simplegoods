package com.simplegoods.simplegoods.service;

import com.simplegoods.simplegoods.model.CartItem;
import com.simplegoods.simplegoods.model.User;
import com.simplegoods.simplegoods.repository.CartItemRepository;
import com.simplegoods.simplegoods.repository.ProductRepository;
import com.simplegoods.simplegoods.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartServiceImpl(CartItemRepository cartRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<CartItem> getCartItems(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return cartRepository.findByUser(user);
    }

    @Override
    public CartItem addToCart(Long userId, Long productId, Integer quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        CartItem cartItem = new CartItem();
        cartItem.setUser(user);
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);

        return cartRepository.save(cartItem);
    }

    @Override
    public CartItem updateCartItem(Long userId, Long productId, Integer quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        CartItem cartItem = cartRepository.findByUserAndProductId(user, productId);
        if (cartItem == null) {
            throw new RuntimeException("Cart item not found for user and product");
        }
        cartItem.setQuantity(quantity);
        return cartRepository.save(cartItem);
    }

    @Override
    public void removeFromCart(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        cartRepository.deleteByUserAndProductId(user, productId);
    }

}
