package com.simplegoods.simplegoods.service;

import com.simplegoods.simplegoods.exception.BadRequestException;
import com.simplegoods.simplegoods.exception.ResourceNotFoundException;
import com.simplegoods.simplegoods.model.*;
import com.simplegoods.simplegoods.repository.CartItemRepository;
import com.simplegoods.simplegoods.repository.OrderRepository;
import com.simplegoods.simplegoods.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Product product;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        product = new Product();
        product.setId(10L);
        product.setPrice(BigDecimal.TEN);

        cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setUser(user);
    }

    @Test
    void placeOrder_ShouldCreateOrder_WhenCartIsNotEmpty() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartItemRepository.findByUser(user)).thenReturn(Collections.singletonList(cartItem));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order order = orderService.placeOrder(1L);

        assertNotNull(order);
        assertEquals(BigDecimal.valueOf(20), order.getTotalAmount());
        assertEquals(1, order.getItems().size());
        verify(cartItemRepository, times(1)).deleteAll(anyList());
    }

    @Test
    void placeOrder_ShouldThrowException_WhenCartIsEmpty() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartItemRepository.findByUser(user)).thenReturn(Collections.emptyList());

        assertThrows(BadRequestException.class, () -> orderService.placeOrder(1L));
    }

    @Test
    void getOrderById_ShouldReturnOrder_WhenUserOwnsIt() {
        Order order = new Order();
        order.setId(100L);
        order.setUser(user);

        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));

        Order result = orderService.getOrderById(100L, 1L);
        assertEquals(100L, result.getId());
    }

    @Test
    void getOrderById_ShouldThrowException_WhenUserDoesNotOwnIt() {
        User otherUser = new User();
        otherUser.setId(2L);

        Order order = new Order();
        order.setId(100L);
        order.setUser(otherUser);

        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));

        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(100L, 1L));
    }
}
