package com.example.authservice.auth;

import com.example.authservice.config.JwtUtill;
import com.example.authservice.utill.GlobalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtill jwtUtill;

    private final RedisTemplate<String, String> redisTemplate;
//    private final UserDetailsService userService;

    private final CustomUserDetailService customUserDetailService;

    private final AuthenticationManager authenticationManager;

    private final UserService userServices;

//    @PostMapping("/login")
//    public ResponseEntity<ApiResponse<AuthResponseDto>> login(@RequestBody LoginRequestDto requestDto) {
//        User userInfo = UserContext.getUser();
//        UserDetails user = userService.loadUserByUsername(requestDto.getUsername());
//
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(requestDto.getUsername(), requestDto.getPassword())
//        );
//
//        AuthResponseDto result =  AuthResponseDto.builder()
//                .accessToken(jwtUtill.generateAccessToken(user.getUsername()))
//                .refreshToken(jwtUtill.generateRefreshToken(user.getUsername()))
//                .build();
//
//        return ResponseEntity.ok(ApiResponse.success(result,"Token generated successfully"));
//    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDto>> login(@RequestBody LoginRequestDto requestDto) {

        // (1) 인증 수행
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.getUserId(), requestDto.getPassword())
        );

        // (2) 인증된 정보로 사용자 데이터 가져오기
        CustomUserDetails user= (CustomUserDetails) authentication.getPrincipal();


//        UserDetails user = customUserDetailService.loadUserByUsername(requestDto.getUserId());


       // (3) 추가 정보 조회 ( idx)
        User res = userServices.getUserIdx(requestDto.getUserId());

        AuthResponseDto result = AuthResponseDto.builder()
                .accessToken(jwtUtill.generateAccessToken(user.getUsername(), user.getIdx(),user.getUserId()))
                .refreshToken(jwtUtill.generateRefreshToken(user.getUsername()))
                .build();

        // 레디스 엑세스 토큰 저장
        redisTemplate.opsForValue().set("ACCESS:" + requestDto.getUserId(), result.getAccessToken(), 3, TimeUnit.HOURS);

        return ResponseEntity.ok(ApiResponse.success(result, "Token generated successfully"));
    }

    @PostMapping("/sign/up")
    public ResponseEntity<GlobalResponse<UserResponseDto>> signUp(@RequestBody UserSignUpRequestDto requestDto) {
        User user = userServices.createUser(requestDto);

        String message = String.format("create success user id  = %s", user.getUserId());
        UserResponseDto res = UserResponseDto.toDto(user);

        return GlobalResponse.successCreate(message,res);
    }



    @PostMapping("duplicate/check/{userId}")
    public ResponseEntity<String> checkUserId(@PathVariable String userId) {
        String res = userServices.checkUserId(userId);
        if (StringUtils.hasText(res)) {

            return ResponseEntity.badRequest().body(res);
        }
        return ResponseEntity.ok().body("success");
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponseDto>> refresh(@RequestParam String refreshToken) {

        if (jwtUtill.validateToken(refreshToken)) {
            String username = jwtUtill.generateRefreshToken(jwtUtill.getUsernameFromToken(refreshToken));
            String updateRefreshToken = jwtUtill.generateRefreshToken(username);
            AuthResponseDto authResponseDto = new AuthResponseDto(jwtUtill.generateAccessToken(username,1L,"test"), updateRefreshToken);
            return ResponseEntity.ok(ApiResponse.success(authResponseDto, "success"));
        }
        return ResponseEntity.badRequest().body(ApiResponse.failure("refresh token failed"));
    }

    @PostMapping("/reissue/access_token")
    public ResponseEntity<ApiResponse<AuthResponseDto>> reissueAccessToken(@RequestParam String refreshToken) {

        String result = jwtUtill.reissueAccessToken(refreshToken);
        AuthResponseDto data = AuthResponseDto.builder().accessToken(result).build();

        return ResponseEntity.ok().body(ApiResponse.success(data, "access token sucees"));
    }

    @PostMapping("/token")
    public String validationToken(@RequestHeader("Authorization") String token) {
        final String VALID = "TOKEN_VALID";
        final String INNVALID = "TOKEN_INVALID";
        // Bearer <token> 형식에서 실제 토큰만 추출
        if (token == null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid token");
        }

        String accessToken = token.substring(7); // 토큰값만 잘라내기


        boolean result = jwtUtill.validateToken(accessToken);
        if (result) {
            return VALID;
        }
        throw new IllegalArgumentException("Invalid token");
    }
}
