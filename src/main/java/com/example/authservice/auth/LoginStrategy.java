package com.example.authservice.auth;

public interface LoginStrategy {
    AuthResponseDto login(LoginRequestDto requestDto);

    LoginType getLoginType();
}
