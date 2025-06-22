package com.simplegoods.simplegoods.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    void testGenerateAccessToken() {
        String username = "testuser";
        Authentication auth = new UsernamePasswordAuthenticationToken(username, "password", new ArrayList<>());
        String token = jwtUtil.generateAccessToken(auth);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        // Verify we can extract the username back
        String extractedUsername = jwtUtil.getUsername(token);
        assertEquals(username, extractedUsername);

        System.out.println("[DEBUG_LOG] Generated token: " + token);
        System.out.println("[DEBUG_LOG] Extracted username: " + extractedUsername);
    }

    @Test
    void testGenerateAccessTokenWithUserDetails() {
        UserDetails userDetails = User.builder()
                .username("testuser")
                .password("password")
                .authorities(new ArrayList<>())
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, "password", userDetails.getAuthorities());
        String token = jwtUtil.generateAccessToken(auth);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        // Verify we can extract the username back
        String extractedUsername = jwtUtil.getUsername(token);
        assertEquals(userDetails.getUsername(), extractedUsername);

        System.out.println("[DEBUG_LOG] Generated token with UserDetails: " + token);
        System.out.println("[DEBUG_LOG] Extracted username: " + extractedUsername);
    }

    @Test
    void testIsTokenValid() {
        String username = "testuser";
        Authentication auth = new UsernamePasswordAuthenticationToken(username, "password", new ArrayList<>());
        String token = jwtUtil.generateAccessToken(auth);

        // Test general validation
        assertTrue(jwtUtil.isTokenValid(token));

        // Test that we can extract username from valid token
        String extractedUsername = jwtUtil.getUsername(token);
        assertEquals(username, extractedUsername);

        System.out.println("[DEBUG_LOG] Token validation tests passed");
    }

    @Test
    void testTokenValidationWithUserDetails() {
        UserDetails userDetails = User.builder()
                .username("testuser")
                .password("password")
                .authorities(new ArrayList<>())
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, "password", userDetails.getAuthorities());
        String token = jwtUtil.generateAccessToken(auth);

        // Test general validation
        assertTrue(jwtUtil.isTokenValid(token));

        // Test that we can extract the correct username
        String extractedUsername = jwtUtil.getUsername(token);
        assertEquals(userDetails.getUsername(), extractedUsername);

        System.out.println("[DEBUG_LOG] UserDetails token validation tests passed");
    }

    @Test
    void testTokenGeneration() {
        String username = "testuser";
        Authentication auth = new UsernamePasswordAuthenticationToken(username, "password", new ArrayList<>());
        String token = jwtUtil.generateAccessToken(auth);

        assertNotNull(token);
        assertTrue(jwtUtil.isTokenValid(token));

        System.out.println("[DEBUG_LOG] Token generation test passed");
    }

    @Test
    void testInvalidToken() {
        String invalidToken = "invalid.token.here";

        assertFalse(jwtUtil.isTokenValid(invalidToken));

        System.out.println("[DEBUG_LOG] Invalid token validation test passed");
    }
}
