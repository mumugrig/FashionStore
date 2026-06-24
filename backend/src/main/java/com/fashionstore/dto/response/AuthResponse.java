package com.fashionstore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Instant accessTokenExpiresAt;
    private UserResponse user;
}
