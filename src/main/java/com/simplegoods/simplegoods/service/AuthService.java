package com.simplegoods.simplegoods.service;

import com.simplegoods.simplegoods.dto.auth.AuthResponse;
import com.simplegoods.simplegoods.dto.auth.LoginRequest;
import com.simplegoods.simplegoods.dto.auth.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest registerRequest);
    AuthResponse login(LoginRequest loginRequest);
}
