package com.example.authservice.auth;

public interface LoginService {
    AuthResponseDto login(LoginType loginType, Object data);

    AuthResponseDto processGoogleLogin(AuthRequestDto request);
}
