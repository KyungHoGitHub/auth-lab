package com.example.authservice.auth;

import com.example.authservice.config.JwtUtill;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class UserTermsRegistrationServiceImpl implements UserTermsRegistrationService {
    private final RedisTemplate<String, GoogleUserCache> redisTemplate;
    private final  UserService userService;
    private final OauthAccountsService oauthAccountsService;


    @Override
    public GoogleUserCache getUserFromRedis(String email) {
        String key = "google_user:" + email;

        try{
            GoogleUserCache googleUserCache = (GoogleUserCache) redisTemplate.opsForValue().get(key);
            if (googleUserCache == null) {
                return null;
            }
            return googleUserCache;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Transactional
    @Override
    public User registerTermsAgreementsUser(String userEmail) {
        // 1. Reids 에서 "이메일"로 임시 저장된 사용자 정보 조회
        GoogleUserCache googleUserCache = getUserFromRedis(userEmail);

        User user = User.builder()
                .userId(generateTempUserId("peng"))
                .username("temp_user")
                .email(googleUserCache.getEmail())
                .build();

        // 2. user 정보 저장
        User res =userService.createOauthUser(user);

        // 3, oauth_accounts 저장
        OauthAccounts oauthAccounts = OauthAccounts.builder()
                .provider("google")
                .providerUserId(googleUserCache.getGoogleId())
                .user(user)
                .build();

        oauthAccountsService.createOauthAccounts(oauthAccounts);


        return res;

        // 유저 정보 조회

        // 토큰 생성
//        jwtUtill.generateAccessToken();
    }

    // Todo 재시도 로직 추가
    public String generateTempUserId(String prefix) {
        // 예시 prefix + 영문 2자리 랜덤 + 숫자 2자리
        // betman + dd + 22  => betmandd22

        final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXY";
        final SecureRandom RANDOM = new SecureRandom();

        StringBuilder sb = new StringBuilder();
        for (int i =0 ; i <4 ; i++){
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }


        int randomNum  = ThreadLocalRandom.current().nextInt(10,99);

        return String.format("%s-%s%d",prefix,sb.toString(),randomNum);
    }
}
