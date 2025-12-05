package com.simplegoods.simplegoods.controller;

import com.simplegoods.simplegoods.config.SecurityConfig;
import com.simplegoods.simplegoods.model.Order;
import com.simplegoods.simplegoods.model.User;
import com.simplegoods.simplegoods.repository.UserRepository;
import com.simplegoods.simplegoods.security.CustomUserDetailsService;
import com.simplegoods.simplegoods.service.OrderService;
import com.simplegoods.simplegoods.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(SecurityConfig.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    private User user;
    private Order order;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        order = new Order();
        order.setId(500L);
        order.setUser(user);
    }

    @Test
    @WithMockUser(username = "testuser")
    void placeOrder_ShouldReturnOrder() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(orderService.placeOrder(1L)).thenReturn(order);

        mockMvc.perform(post("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(500));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getUserOrders_ShouldReturnList() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(orderService.getUserOrders(1L)).thenReturn(Collections.singletonList(order));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(500));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getOrder_ShouldReturnOrder() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(orderService.getOrderById(500L, 1L)).thenReturn(order);

        mockMvc.perform(get("/api/orders/500"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(500));
    }
}
