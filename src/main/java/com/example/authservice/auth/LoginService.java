package com.example.authservice.auth;

public interface LoginService {
    AuthResponseDto login(LoginRequestDto loginRequestDto);

    AuthResponseDto processGoogleLogin(GoogleLoginRequest request);
}
