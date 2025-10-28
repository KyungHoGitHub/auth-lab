package com.example.authservice.auth;

public interface LoginStrategy {
    AuthResponse login(Object requestDto);

    LoginType getLoginType();
}
