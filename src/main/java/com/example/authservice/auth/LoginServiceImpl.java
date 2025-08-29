package com.example.authservice.auth;

import com.example.authservice.config.JwtUtill;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LoginServiceImpl implements LoginService {
    private final Map<LoginType, LoginStrategy> strategies;

    private final GoogleTokenVerifier googleTokenVerifier;

    private final UserRepository userRepository;
    private final JwtUtill jwtUtill;

    public LoginServiceImpl(List<LoginStrategy> strategyList, GoogleTokenVerifier googleTokenVerifier, UserRepository userRepository, JwtUtill jwtUtill){
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(LoginStrategy::getLoginType, Function.identity()));
        this.googleTokenVerifier = googleTokenVerifier;
        this.userRepository = userRepository;
        this.jwtUtill = jwtUtill;
    }

    @Override
    public AuthResponseDto login(LoginRequestDto loginRequestDto) {
        LoginStrategy strategy = strategies.get(loginRequestDto.getLoginType());
        if(strategy == null){
            throw new IllegalArgumentException("Unknown login type: " + loginRequestDto.getLoginType());
        }
        return strategy.login(loginRequestDto);
    }

    @Override
    public AuthResponseDto processGoogleLogin(GoogleLoginRequest request) {
        try {
            // 1. Google 토큰 검증 및 사용자 정보 추출


            GoogleUserInfo googleUserInfo = googleTokenVerifier.verifyAccessTokenAndGetUserInfo(request.getToken());



            // 2. 이메일로 기존 사용자 조회
            User user = userRepository.findByEmail(googleUserInfo.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));


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

//    private User createNewUser(GoogleUserInfo googleUserInfo) {
//        User newUser = User.builder()
//                .email(googleUserInfo.getEmail())
//                .username(googleUserInfo.getName())
//                .
//    }
}
