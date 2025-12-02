package com.simplegoods.simplegoods.service;

import com.simplegoods.simplegoods.model.CartItem;
import com.simplegoods.simplegoods.model.Product;
import com.simplegoods.simplegoods.model.User;
import com.simplegoods.simplegoods.repository.CartItemRepository;
import com.simplegoods.simplegoods.repository.ProductRepository;
import com.simplegoods.simplegoods.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceImplTest {

    @Mock
    private CartItemRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    private User user;
    private Product product;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(100.0));

        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setUser(user);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
    }

    @Test
    void getCartItems_ShouldReturnListOfCartItems() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Arrays.asList(cartItem));

        List<CartItem> items = cartService.getCartItems(1L);

        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(cartItem, items.get(0));
        verify(cartRepository, times(1)).findByUser(user);
    }

    @Test
    void getCartItems_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            cartService.getCartItems(1L);
        });

        assertEquals("User not found with id: 1", exception.getMessage());
        verify(cartRepository, times(0)).findByUser(any(User.class));
    }

    @Test
    void addToCart_ShouldReturnCartItem() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepository.save(any(CartItem.class))).thenReturn(cartItem);

        CartItem addedItem = cartService.addToCart(1L, 1L, 2);

        assertNotNull(addedItem);
        assertEquals(cartItem.getProduct().getName(), addedItem.getProduct().getName());
        verify(cartRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    void addToCart_WhenProductNotFound_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            cartService.addToCart(1L, 1L, 2);
        });

        assertEquals("Product not found with id: 1", exception.getMessage());
        verify(cartRepository, times(0)).save(any(CartItem.class));
    }

    @Test
    void updateCartItem_ShouldReturnUpdatedCartItem() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserAndProductId(user, 1L)).thenReturn(cartItem);
        when(cartRepository.save(cartItem)).thenReturn(cartItem);

        CartItem updatedItem = cartService.updateCartItem(1L, 1L, 5);

        assertNotNull(updatedItem);
        assertEquals(5, updatedItem.getQuantity());
        verify(cartRepository, times(1)).save(cartItem);
    }

    @Test
    void updateCartItem_WhenItemNotFound_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserAndProductId(user, 1L)).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            cartService.updateCartItem(1L, 1L, 5);
        });

        assertEquals("Cart item not found for user and product", exception.getMessage());
        verify(cartRepository, times(0)).save(any(CartItem.class));
    }

    @Test
    void removeFromCart_ShouldDeleteCartItem() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(cartRepository).deleteByUserAndProductId(user, 1L);

        cartService.removeFromCart(1L, 1L);

        verify(cartRepository, times(1)).deleteByUserAndProductId(user, 1L);
    }
}
