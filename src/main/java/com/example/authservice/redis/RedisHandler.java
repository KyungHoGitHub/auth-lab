package com.example.authservice.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RedisHandler {

    private final RedisTemplate<String, String> redisTemplate;

    @GetMapping("/home")
    public String home() {
        redisTemplate.opsForValue().set("home:visit", "User visited home at " + System.currentTimeMillis());
        return "Welcome to the home page!";
    }

    @GetMapping("/public")
    public String publicPage() {
        return "This is a public page.";
    }
}
