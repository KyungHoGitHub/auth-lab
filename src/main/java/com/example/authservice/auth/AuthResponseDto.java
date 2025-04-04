package com.example.authservice.auth;

import com.example.authservice.config.JwtUtill;
import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDto {

    private String accessToken;

    private String refreshToken;


}
