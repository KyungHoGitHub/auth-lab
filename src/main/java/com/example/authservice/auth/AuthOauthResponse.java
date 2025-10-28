package com.example.authservice.auth;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("OAUTH")
public record AuthOauthResponse(
        String accessToken,
        String refreshToken,
        String oauthEmail,
        String responseCode
) implements AuthResponse {
}
