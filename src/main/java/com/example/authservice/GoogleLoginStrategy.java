package com.example.authservice;

import com.example.authservice.auth.*;
import com.example.authservice.config.JwtUtill;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GoogleLoginStrategy implements LoginStrategy {
    private final GoogleTokenVerifier googleTokenVerifier;

    private final UserRepository userRepository;
    private final JwtUtill jwtUtill;
    private static final Logger logger = LoggerFactory.getLogger(GoogleLoginStrategy.class);

    @Override
    public AuthResponseDto login(Object requestDto) {
        GoogleLoginData data = (GoogleLoginData) requestDto;


        try {
            GoogleUserInfo googleUserInfo = googleTokenVerifier.verifyAccessTokenAndGetUserInfo(data.getToken());

            logger.info("Google login attempt for email: {}", googleUserInfo.getEmail());

            // 2. 이메일로 기존 사용자 조회
            User user = userRepository.findByEmail(googleUserInfo.getEmail())
                    .map(exisitingUser -> updateExistingUser(exisitingUser,googleUserInfo))
                    .orElseGet(()-> createNewUser(googleUserInfo));


            // 3. JWT 토큰 생성
            String accessToken = jwtUtill.generateAccessToken(
                    user.getUsername(),
                    user.getIdx(),
                    user.getUserId(),
                    user.getRole()
            );

            return AuthResponseDto.builder()

                    .accessToken(accessToken)

                    .build();

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
    @Override
    public LoginType getLoginType() {
        return LoginType.GOOGLE;
    }
}
