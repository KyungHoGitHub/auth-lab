package com.example.authservice.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class TokenPolicyCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final TokenPolicyRepository tokenPolicyRepository;

    @Cacheable(value = "tokenPolicy", key = "'accessToken:' + #idx", unless = "#result == null")
    public Long getAccessToken(Long idx) {
        Object result = tokenPolicyRepository.findById(idx)
                .map(TokenPolicy::getAccessTokenExp)
                .orElseThrow(() -> new RuntimeException("Token policy not found for idx: " + idx));

        // 캐시에서 가져온 값이 Integer일 수 있으므로 안전하게 변환
        if (result instanceof Number) {
            return ((Number) result).longValue();
        }
        return (Long) result;
    }


//    @Cacheable(value = "tokenPolicy", key = "'refreshToken:' + #idx", unless = "#result == null")
//    public Integer getRefreshTokenExp(Long idx) {
//        return tokenPolicyRepository.findById(idx)
//                .map(TokenPolicy::getRefreshTokenExp)
//                .orElseThrow(() -> new RuntimeException("Token policy not found for idx: " + idx));
//    }
}
