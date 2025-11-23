package com.simplegoods.simplegoods.dto.auth;

import lombok.Getter;

@Getter
public class AuthResponse {

    private final String token;
    private final String tokenType = "Bearer";
    private final Long userId;

    public AuthResponse(String token, Long userId) {
        this.token = token;
        this.userId = userId;
    }

}
