package com.example.authservice.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {

    @Schema(description = "유저 아이디", example = "YuKyungHo111")
    private String userId;

    @Schema(description = "유저 패스워드", example = "@@@aaa1234")
    private String password;
}
