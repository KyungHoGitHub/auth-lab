package com.example.authservice.auth;

public interface LoginStrategy {
    AuthResponseDto login(Object requestDto);

    LoginType getLoginType();
}
