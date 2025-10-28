package com.example.authservice.auth;

import com.example.authservice.config.JwtUtill;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDto {

    @Schema(description = "엑세스 토큰", example = "aaaaa")
    private String accessToken;

    @Schema(description = "리프레쉬 토큰", example = "aaa")
    private String refreshToken;

    private String oauthEmail;

    private String responseCode;
}
