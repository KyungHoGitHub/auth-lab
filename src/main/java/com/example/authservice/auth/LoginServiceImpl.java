package com.example.authservice.auth;

import com.example.authservice.config.JwtUtill;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {
    private  Map<LoginType, LoginStrategy> strategies;
    private final GoogleTokenVerifier googleTokenVerifier;
    private final UserRepository userRepository;
    private final JwtUtill jwtUtill;

    private final List<LoginStrategy> strategiesList;
    private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

//    public LoginServiceImpl(List<LoginStrategy> strategyList, GoogleTokenVerifier googleTokenVerifier, UserRepository userRepository, JwtUtill jwtUtill) {
//        this.strategies = strategyList.stream()
//                .collect(Collectors.toMap(LoginStrategy::getLoginType, Function.identity()));
//        this.googleTokenVerifier = googleTokenVerifier;
//        this.userRepository = userRepository;
//        this.jwtUtill = jwtUtill;
//    }

    @PostConstruct
    public void initStrategies(){
        this.strategies = strategiesList.stream()
                .collect(Collectors.toMap(LoginStrategy::getLoginType, Function.identity()));
    }

    @Override
    public AuthResponseDto login(LoginType loginType, Object data) {
        LoginStrategy strategy = strategies.get(loginType);

        if (strategy == null) {
            throw new IllegalArgumentException("Unknown login type: " + loginType);
        }
        return strategy.login(data);
    }

    @Override
    public AuthResponseDto processGoogleLogin(AuthRequestDto request) {
        try {
            // 1. Google 토큰 검증 및 사용자 정보 추출
            GoogleUserInfo googleUserInfo = googleTokenVerifier.verifyAccessTokenAndGetUserInfo(null);

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
}
