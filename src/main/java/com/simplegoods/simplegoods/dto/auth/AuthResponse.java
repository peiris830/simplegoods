package com.simplegoods.simplegoods.dto.auth;

import lombok.Getter;

@Getter
public class AuthResponse {

    private final String token;
    private final String tokenType = "Bearer";

    public AuthResponse(String token) {
        this.token = token;
    }

}
