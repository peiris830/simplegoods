package com.simplegoods.simplegoods.service;

import com.simplegoods.simplegoods.dto.auth.AuthResponse;
import com.simplegoods.simplegoods.dto.auth.LoginRequest;
import com.simplegoods.simplegoods.dto.auth.RegisterRequest;
import com.simplegoods.simplegoods.model.User;
import com.simplegoods.simplegoods.repository.UserRepository;
import com.simplegoods.simplegoods.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public AuthResponse register(RegisterRequest registerRequest) {
        userRepository.findByUsername(registerRequest.getUsername())
                .ifPresent(user -> {
                    throw new RuntimeException("Username already taken");
                });
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email already taken");
        }

        User user = new User();
        user.setUsername((registerRequest.getUsername()));
        user.setEmail(registerRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));

        User savedUser = userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(registerRequest.getUsername(), registerRequest.getPassword()));
        String token = jwtUtil.generateAccessToken(authentication);

        return new AuthResponse(token, savedUser.getId());
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        String token = jwtUtil.generateAccessToken(authentication);

        // Fetch user to get ID
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new AuthResponse(token, user.getId());
    }

}
