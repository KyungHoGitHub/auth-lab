package com.example.authservice.auth;

import com.example.authservice.config.JwtUtill;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class GoogleLoginStrategy implements LoginStrategy {
    private final GoogleTokenVerifier googleTokenVerifier;
    private final UserRepository userRepository;
    private final JwtUtill jwtUtill;
    private static final Logger logger = LoggerFactory.getLogger(GoogleLoginStrategy.class);
    @Qualifier("googleUserCacheRedisTemplate")
    private final RedisTemplate<String, GoogleUserCache> redisTemplate;
    private final RedisTemplate<String, String> stringRedisTemplate;
    private static final String TEMP_USERS_SET = "google_users_temp";

    @Override
    public AuthResponseDto login(Object requestDto) {
        GoogleOauthTokenRequest googleOauthTokenRequest = (GoogleOauthTokenRequest) requestDto;

        try {
            GoogleUserInfo googleUserInfo = googleTokenVerifier.verifyAccessTokenAndGetUserInfo(googleOauthTokenRequest.token());

            logger.info("Google login attempt for email: {}", googleUserInfo.getEmail());

            // 2. 이메일로 기존 사용자 조회
            User user = userRepository.findByEmail(googleUserInfo.getEmail())
//                    .map(exisitingUser -> updateExistingUser(exisitingUser,googleUserInfo))
                    .orElse(null);

            if (user != null) {
                String accessToken = jwtUtill.generateAccessToken(
                    user.getUsername(),
                    user.getIdx(),
                    user.getUserId(),
                    user.getRole()
            );
                return AuthResponseDto.builder()

                        .accessToken(accessToken)

                        .build();
            } else {
            // 신규 사용자 처리
            return handleNewUser(googleUserInfo);
        }

        } catch (Exception e) {
            return AuthResponseDto.builder()
                    .accessToken(null)
                    .build();
        }
    }
    private User updateExistingUser(User existingUser, GoogleUserInfo googleUserInfo) {
        // 구글 아이디가 없으면 -> 구글 Oauth 아직 미가입자
        if (existingUser.getGoogleId() == null) {
            existingUser.setGoogleId(googleUserInfo.getGoogleId());
        }


        return userRepository.save(User.builder()
                .loginType(LoginType.GOOGLE)
                .email(googleUserInfo.getEmail())
                .build());
    }

    private User createNewUser(GoogleUserInfo googleUserInfo) {
        User newUser = User.builder()
                .email(googleUserInfo.getEmail())
                .username(googleUserInfo.getName())
                .loginType(LoginType.GOOGLE)
                .googleId(googleUserInfo.getGoogleId())
                .build();
        User user = userRepository.save(newUser);
        logger.info("New user created for email: {}", user.getEmail());
        return user;
    }

    private AuthResponseDto handleNewUser(GoogleUserInfo googleUserInfo) {

        saveToRedis(googleUserInfo);

        return AuthResponseDto.builder()
                .accessToken(googleUserInfo.getGoogleId())
                .build();
    }

    private void saveToRedis(GoogleUserInfo googleUserInfo) {
        String key = "google_user:" + googleUserInfo.getEmail();

        GoogleUserCache googleUserCache = GoogleUserCache.builder()
                .googleId(googleUserInfo.getGoogleId())
                .email(googleUserInfo.getEmail())
                .picture(googleUserInfo.getPicture())
                .emailVerified(googleUserInfo.getEmailVerified())
                .build();

        try {
            // GoogleUserCache 저장
            redisTemplate.opsForValue().set(key, googleUserCache, 1, TimeUnit.HOURS);
            // 임시 사용자 목록에 이메일 추가
            stringRedisTemplate.opsForSet().add(TEMP_USERS_SET, googleUserInfo.getEmail());
            // Set에도 TTL 설정 (동기화 보장)
            stringRedisTemplate.expire(TEMP_USERS_SET, 1, TimeUnit.HOURS);
            logger.info("Google user info stored in Redis for key: {}, added to set: {}", key, TEMP_USERS_SET);
        } catch (Exception e) {
            logger.error("Failed to store in Redis: {}", e.getMessage(), e);
            throw new RuntimeException("Redis 저장 실패", e);
        }
    }
    @Override
    public LoginType getLoginType() {
        return LoginType.GOOGLE;
    }
}
