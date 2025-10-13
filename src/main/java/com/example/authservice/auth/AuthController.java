package com.example.authservice.auth;

import com.example.authservice.config.JwtUtill;
import com.example.authservice.utill.GlobalResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Auth", description = "인증 관련 API")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class AuthController {

    private final JwtUtill jwtUtill;

    private final RedisTemplate<String, String> redisTemplate;

    private final CustomUserDetailService customUserDetailService;

    private final AuthenticationManager authenticationManager;

    private final TokenPolicyService tokenPolicyService;

    private final UserService userServices;

    private final LoginService loginService;

    private final LoginDataFactory loginDataFactory;

    private final TokenService tokenService;

    @PostMapping("google-login")
    public ResponseEntity<?> googleLogin(@RequestBody AuthRequestDto request){


        AuthResponseDto authResponseDto = loginService.processGoogleLogin(request);

        System.out.println(authResponseDto);
        return null;
    }

    @Operation(
            summary = "로그인 API",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "로그인 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AuthResponseDto.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "성공 예시",
                                                    summary = "간단한 예제",
                                                    value = "{\"accessToken\":\"암호화 된 엑세스 토큰\",\"refreshToken\":\"암호화 된 리프레쉬 토큰\",\"message\": \"Token generated successfully\"}"
                                            )
                                    }
                            )
                    )
            }
    )
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDto>> login(@RequestBody AuthRequestDto requestDto ,BindingResult bindingResult) {
//        if (bindingResult.hasErrors()) {
//            String errorMessage = Optional.ofNullable(bindingResult.getFieldError())
//                    .map(FieldError::getDefaultMessage)
//                    .orElse("잘못된 요청입니다.");
//
//            return ResponseEntity.badRequest().body(ApiResponse.failure(errorMessage));
//        }

        LoginType loginType = requestDto.getLoginType();

        Object loginData = loginDataFactory.createLoginData(requestDto);

         AuthResponseDto result = loginService.login(loginType , loginData);
        return ResponseEntity.ok(ApiResponse.success(result, "Token generated successfully"));
    }

    @Operation(
            summary = "회원가입 API",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "201",
                            description = "회원가입 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponseDto.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "성공 예시",
                                                    summary = "간단한 예제",
                                                    value = "{\"user\":\"생성된 유저 정보\",\"message\": \"create success user id = 유저 아이디\"}"
                                            )
                                    }
                            )
                    )
            }
    )
    @PostMapping("/sign/up")
    public ResponseEntity<GlobalResponse<UserResponseDto>> signUp(@RequestBody UserSignUpRequestDto requestDto) {
        User user = userServices.createUser(requestDto);

        String message = String.format("create success user id  = %s", user.getUserId());
        UserResponseDto res = UserResponseDto.toDto(user);

        return GlobalResponse.successCreate(message,res);
    }


    @Operation(
            summary = "아이디 중복 체크 API",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "중복 체크 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "성공 예시",
                                                    summary = "간단한 예제",
                                                    value = "{\"message\": \"success\"}"
                                            )
                                    }
                            )
                    )
            }
    )
    @PostMapping("duplicate/check/{userId}")
    public ResponseEntity<String> checkUserId(@Parameter(description = "조회할 유저 아이디", example = "authBot12") @PathVariable String userId) {
        String res = userServices.checkUserId(userId);
        if (StringUtils.hasText(res)) {

            return ResponseEntity.badRequest().body(res);
        }
        return ResponseEntity.ok().body("success");
    }

    @Operation(
            summary = "리프레쉬 토큰 재발급 API",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AuthResponseDto.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "성공 예시",
                                                    summary = "간단한 예제",
                                                    value = "{\"accessToken\": \"엑세스 토큰\",\"refreshToken\": \"리프레쉬 토큰\",\"message\": \"success\"}"
                                            )
                                    }
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "실패",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "실패 예시",
                                                    summary = "간단한 예제",
                                                    value = "{\"message\": \"refresh token failed\"}"
                                            )
                                    }
                            )
                    )
            }
    )
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponseDto>> refresh(@Parameter(description = "리프레쉬 토큰", example = "refresh token") @RequestParam String refreshToken) {

        if (jwtUtill.validateToken(refreshToken)) {
            String username = jwtUtill.generateRefreshToken(jwtUtill.getUsernameFromToken(refreshToken));
            String updateRefreshToken = jwtUtill.generateRefreshToken(username);
            AuthResponseDto authResponseDto = new AuthResponseDto(jwtUtill.generateAccessToken(username,1L,"test","test"), updateRefreshToken);
            return ResponseEntity.ok(ApiResponse.success(authResponseDto, "success"));
        }
        return ResponseEntity.badRequest().body(ApiResponse.failure("refresh token failed"));
    }

    @Operation(
            summary = "엑세스 토큰 재발급 API",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AuthResponseDto.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "성공 예시",
                                                    summary = "간단한 예제",
                                                    value = "{\"accessToken\": \"엑세스 토큰\",\"refreshToken\": \"리프레쉬 토큰\",\"message\": \"access token success\"}"
                                            )
                                    }
                            )
                    )
            }
    )
    @PostMapping("/reissue/access_token")
    public ResponseEntity<ApiResponse<AuthResponseDto>> reissueAccessToken(@RequestParam String refreshToken) {

        String result = jwtUtill.reissueAccessToken(refreshToken);
        AuthResponseDto data = AuthResponseDto.builder().accessToken(result).build();

        return ResponseEntity.ok().body(ApiResponse.success(data, "access token success"));
    }

    @Operation(
            summary = "토큰 검증 API",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AuthResponseDto.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "성공 예시",
                                                    summary = "간단한 예제",
                                                    value = "{\"data\": \"true\"}"
                                            )
                                    }
                            )
                    )
            }
    )
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

    @PostMapping("/token-policy")
    public ResponseEntity<String> createTokenPolicy(@RequestBody TokenPolicyRequestDTO requestDTO, HttpServletRequest request) {
        tokenPolicyService.createTokenPolicy(requestDTO);
        return ResponseEntity.ok("success");
    }

    @PostMapping("/temp/token")
    public ResponseEntity<String> getTempToken(){
        String accessToken = tokenService.getTempToken();
        return ResponseEntity.ok(accessToken);
    }
}
