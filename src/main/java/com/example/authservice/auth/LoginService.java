package com.example.authservice.auth;

public interface LoginService {
    AuthResponse login(LoginType loginType, Object data);

    AuthResponseDto processGoogleLogin(AuthRequestDto request);
}
