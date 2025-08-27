package com.example.authservice.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class CacheController {

    private final CacheManager cacheManager;
    private final RedisTemplate redisTemplate;

    @PostMapping("/admin/cache/clear/token-policy")
    public ResponseEntity<Map<String, Object>> clearTokenPolicyCache() {
        Cache cache = cacheManager.getCache("tokenPolicy");
        Map<String, Object> result = new HashMap<>();

        if (cache != null) {
            // 클리어 전 Redis 키 확인
            Set<String> keysBefore = redisTemplate.keys("*tokenPolicy*");
            result.put("keysBefore", keysBefore);

            cache.clear();

            // 클리어 후 Redis 키 확인
            Set<String> keysAfter = redisTemplate.keys("*tokenPolicy*");
            result.put("keysAfter", keysAfter);

            result.put("message", "Cache clear attempted");
            result.put("cleared", keysBefore.size() - keysAfter.size());
        } else {
            result.put("message", "Cache not found");
        }

        return ResponseEntity.ok(result);
    }
}
