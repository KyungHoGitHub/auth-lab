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

        UserDetails user = customUserDetailService.loadUserByUsername(requestDto.getUserId());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.getUserId(), requestDto.getPassword())
        );

        AuthResponseDto result = AuthResponseDto.builder()
                .accessToken(jwtUtill.generateAccessToken(user.getUsername()))
                .refreshToken(jwtUtill.generateRefreshToken(user.getUsername()))
                .build();

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



    @PostMapping("duplicate/check/userId")
    public ResponseEntity<String> checkUserId(@RequestBody String userId) {
        String res = userServices.checkUserId(userId);
        if ((res.isEmpty())) {

            return ResponseEntity.ok(res);
        }
        return ResponseEntity.badRequest().body("userId is empty");
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponseDto>> refresh(@RequestParam String refreshToken) {

        if (jwtUtill.validateToken(refreshToken)) {
            String username = jwtUtill.generateRefreshToken(jwtUtill.getUsernameFromToken(refreshToken));
            String updateRefreshToken = jwtUtill.generateRefreshToken(username);
            AuthResponseDto authResponseDto = new AuthResponseDto(jwtUtill.generateAccessToken(username), updateRefreshToken);
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
