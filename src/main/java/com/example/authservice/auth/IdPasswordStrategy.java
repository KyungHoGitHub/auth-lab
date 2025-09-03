package com.example.authservice.auth;

import com.example.authservice.config.JwtUtill;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class IdPasswordStrategy implements LoginStrategy {

    private final JwtUtill jwtUtill;

    private final AuthenticationManager authenticationManager;

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public AuthResponseDto login(Object loginData) {
        IdPasswordLoginData data = (IdPasswordLoginData) loginData;

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(data.getUserId(), data.getPassword())
        );

        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        AuthResponseDto result = AuthResponseDto.builder()
                .accessToken(jwtUtill.generateAccessToken(user.getUsername(), user.getIdx(), user.getUserId(), user.getRole()))
                .refreshToken(jwtUtill.generateRefreshToken(user.getUsername()))
                .build();

        redisTemplate.opsForValue().set("ACCESS:" + data.getUserId(), result.getAccessToken(), 3, TimeUnit.HOURS);

        return result;
    }

    @Override
    public LoginType getLoginType() {
        return LoginType.ID_PASSWORD;
    }
}
