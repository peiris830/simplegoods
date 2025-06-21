package com.simplegoods.simplegoods.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        String testSecret = "ec60266cc5fa0d0f17afb337a12b23beef243180";
        // 1 hour
        long testExpirationMs = 3600000;
        jwtUtil = new JwtUtil(testSecret, testExpirationMs);
    }

    @Test
    void testGenerateTokenWithUsername() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        // Verify we can extract the username back
        String extractedUsername = jwtUtil.extractUsername(token);
        assertEquals(username, extractedUsername);
        
        System.out.println("[DEBUG_LOG] Generated token: " + token);
        System.out.println("[DEBUG_LOG] Extracted username: " + extractedUsername);
    }

    @Test
    void testGenerateTokenWithUserDetails() {
        UserDetails userDetails = User.builder()
                .username("testuser")
                .password("password")
                .authorities(new ArrayList<>())
                .build();
        
        String token = jwtUtil.generateToken(userDetails);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        // Verify we can extract the username back
        String extractedUsername = jwtUtil.extractUsername(token);
        assertEquals(userDetails.getUsername(), extractedUsername);
        
        System.out.println("[DEBUG_LOG] Generated token with UserDetails: " + token);
        System.out.println("[DEBUG_LOG] Extracted username: " + extractedUsername);
    }

    @Test
    void testValidateToken() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);
        
        // Test validation with username
        assertTrue(jwtUtil.validateToken(token, username));
        
        // Test validation with wrong username
        assertFalse(jwtUtil.validateToken(token, "wronguser"));
        
        // Test general validation
        assertTrue(jwtUtil.validateToken(token));
        
        System.out.println("[DEBUG_LOG] Token validation tests passed");
    }

    @Test
    void testValidateTokenWithUserDetails() {
        UserDetails userDetails = User.builder()
                .username("testuser")
                .password("password")
                .authorities(new ArrayList<>())
                .build();
        
        String token = jwtUtil.generateToken(userDetails);
        
        // Test validation with correct UserDetails
        assertTrue(jwtUtil.validateToken(token, userDetails));
        
        // Test validation with different UserDetails
        UserDetails differentUser = User.builder()
                .username("differentuser")
                .password("password")
                .authorities(new ArrayList<>())
                .build();
        
        assertFalse(jwtUtil.validateToken(token, differentUser));
        
        System.out.println("[DEBUG_LOG] UserDetails token validation tests passed");
    }

    @Test
    void testExtractExpiration() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);
        
        assertNotNull(jwtUtil.extractExpiration(token));
        
        System.out.println("[DEBUG_LOG] Token expiration: " + jwtUtil.extractExpiration(token));
    }

    @Test
    void testInvalidToken() {
        String invalidToken = "invalid.token.here";
        
        assertFalse(jwtUtil.validateToken(invalidToken));
        
        System.out.println("[DEBUG_LOG] Invalid token validation test passed");
    }
}