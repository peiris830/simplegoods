package com.simplegoods.simplegoods.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplegoods.simplegoods.config.SecurityConfig;
import com.simplegoods.simplegoods.model.CartItem;
import com.simplegoods.simplegoods.model.Product;
import com.simplegoods.simplegoods.model.User;
import com.simplegoods.simplegoods.repository.UserRepository;
import com.simplegoods.simplegoods.security.CustomUserDetailsService;
import com.simplegoods.simplegoods.service.CartService;
import com.simplegoods.simplegoods.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false) // Bypassing filter for unit test simplicity, relying on WithMockUser
@Import(SecurityConfig.class)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    private User user;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Product product = new Product();
        product.setId(10L);
        product.setName("Test Product");
        product.setPrice(BigDecimal.TEN);

        cartItem = new CartItem();
        cartItem.setId(100L);
        cartItem.setUser(user);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
    }

    @Test
    @WithMockUser(username = "testuser")
    void getCart_ShouldReturnItems() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cartService.getCartItems(1L)).thenReturn(Collections.singletonList(cartItem));

        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].quantity").value(2));
    }

    @Test
    @WithMockUser(username = "testuser")
    void addToCart_ShouldReturnItem() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cartService.addToCart(eq(1L), eq(10L), eq(2))).thenReturn(cartItem);

        mockMvc.perform(post("/api/cart")
                .param("productId", "10")
                .param("quantity", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(2));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateCartItem_ShouldReturnUpdatedItem() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cartService.updateCartItem(eq(1L), eq(10L), eq(5))).thenReturn(cartItem);

        mockMvc.perform(put("/api/cart")
                .param("productId", "10")
                .param("quantity", "5"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser")
    void removeFromCart_ShouldReturnNoContent() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        mockMvc.perform(delete("/api/cart/10"))
                .andExpect(status().isNoContent());
    }
}
