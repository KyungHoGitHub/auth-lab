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
    private static final Logger logger = LoggerFactory.getLogger(GoogleLoginStrategy.class);
    private static final String TEMP_USERS_SET = "google_users_temp";
    private final GoogleTokenVerifier googleTokenVerifier;

    private final JwtUtill jwtUtill;
    private final RedisTemplate<String, String> stringRedisTemplate;

    @Qualifier("googleUserCacheRedisTemplate")
    private final RedisTemplate<String, GoogleUserCache> redisTemplate;

    private final OauthAccountsService oauthAccountsService;
    private final UserService userService;


    @Override
    public AuthResponse login(Object requestDto) {
        if (!(requestDto instanceof GoogleOauthTokenRequest googleOauthTokenRequest)) {
            throw new IllegalArgumentException("requestDto must be instance of GoogleOauthTokenRequest");
        }

        GoogleUserInfo googleUserInfo = googleTokenVerifier.verifyAccessTokenAndGetUserInfo(googleOauthTokenRequest.token());
        logger.info("Google login attempt for email: {}", googleUserInfo.getEmail());

        // 1. 기존 구글 로그인 인증했는지 조회
//            User user = processOauthLogin(googleUserInfo);  // 잠시 정지
        // 2. 이메일로 기존 사용자 조회
//            User user = userRepository.findByEmail(googleUserInfo.getEmail())
//                    .orElse(null);

        loginProcess(googleUserInfo);

        User user = userService.findByEmail(googleUserInfo.getEmail()).orElse(null);
        if (user == null) {
            return new AuthOauthResponse(null, null, googleUserInfo.getEmail(), "term-required");
        }

        String accessToken = jwtUtill.generateAccessToken(
                user.getUsername(),
                user.getIdx(),
                user.getUserId(),
                user.getRole());

        return new AuthIdPasswordResponse(accessToken, "");
    }

//    private User updateExistingUser(User existingUser, GoogleUserInfo googleUserInfo) {
//        // 구글 아이디가 없으면 -> 구글 Oauth 아직 미가입자
//        if (existingUser.getGoogleId() == null) {
//            existingUser.setGoogleId(googleUserInfo.getGoogleId());
//        }
//
//
//        return userRepository.save(User.builder()
//                .loginType(LoginType.GOOGLE)
//                .email(googleUserInfo.getEmail())
//                .build());
//    }

//    private User createNewUser(GoogleUserInfo googleUserInfo) {
//        User newUser = User.builder()
//                .email(googleUserInfo.getEmail())
//                .username(googleUserInfo.getName())
//                .loginType(LoginType.GOOGLE)
//                .googleId(googleUserInfo.getGoogleId())
//                .build();
//        User user = userRepository.save(newUser);
//        logger.info("New user created for email: {}", user.getEmail());
//        return user;
//    }

    private AuthResponseDto handleNewUser(GoogleUserInfo googleUserInfo) {

        saveToRedis(googleUserInfo);

        return AuthResponseDto.builder()
                .oauthEmail(googleUserInfo.getEmail())
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

//    private User processOauthLogin(GoogleUserInfo googleUserInfo) {
//        // 1. 기존에 구글 Oauth 인증 연동 되어있는 계정인지 데이터 조회
//        OauthAccounts oauthAccounts = oauthAccountsRepository.findByProviderUserId(googleUserInfo.getGoogleId()).orElse(null);
//
//        // 2. 기존에 인증 사용하지 않았을 경우
//        if (oauthAccounts == null) {
//            // 2.1 서비스 기존 회원인지 '이메일' 로 조회
//            User user = userRepository.findByEmail(googleUserInfo.getEmail()).orElse(null);
//
//
//            if (user != null) {
//                oauthAccountsRepository.save(OauthAccounts.builder()
//                        .provider("google")
//                        .providerUserId(googleUserInfo.getGoogleId())
//                        .build());
//            }
//            return user;
//
//        } else {
//            return userRepository.findByEmail(googleUserInfo.getEmail()).orElse(null);
//        }
//    }

    private void loginProcess(GoogleUserInfo googleUserInfo) {
        //  자자 구글 정보 온다
        //  googleInfo (1.googleId, 2.email, 3.name)
        String googleId = googleUserInfo.getGoogleId();
        String googleEmail = googleUserInfo.getEmail();

        if (oauthAccountsService.existOauthAccountByProviderUserId(googleId)) {
            return;
        }

        userService.findByEmail(googleEmail)
                .ifPresent(existngUser -> oauthAccountsService.linkCreateOauthAccounts(googleId)
                );
    }


    // 구글 로그인 시 기존에 유저 정보 있을때 oauthAccount 추가
    public void linkOauthAccount(String providerUserId) {

    }

    @Override
    public LoginType getLoginType() {
        return LoginType.GOOGLE;
    }
}
