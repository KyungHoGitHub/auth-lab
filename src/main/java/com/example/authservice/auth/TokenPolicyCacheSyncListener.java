package com.example.authservice.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class TokenPolicyCacheSyncListener {
//    private final CacheManager cacheManager;
//
//    @KafkaListener(topics = "token-policy-topic", groupId = "auth-service-group")
//    public void handleTokenPolicyEvent(TokenPolicyEvent event) {
//        Cache cache =  cacheManager.getCache("tokenPolicy");
//        if (cache == null) {
//            return;
//        }
//
//        String cacheKey = "accessToken" + event.getIdx();
//        if ("UPDATE".equals(event.getAction())){
//            cache.put(cacheKey, event.getAccessTokenExp());
//        }
//    }
//}
