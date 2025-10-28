package com.example.authservice.auth;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("ID-PW")
public record AuthIdPasswordResponse(String accessToken, String refreshToken) implements AuthResponse {
}
