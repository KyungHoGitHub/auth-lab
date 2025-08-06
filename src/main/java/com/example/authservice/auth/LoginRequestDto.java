package com.example.authservice.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {

    @NotBlank(message = "아이디는 필수 입력 항목입니다.")
    @Size(max = 20, message = "아이디는 최대 20자 이하이어야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "아이디는 영문, 숫자, 밑줄(_)만 사용할 수 있습니다.")
    private String userId;

    @NotBlank(message = "패스워드는 필수 입력 항목입니다.")
    private String password;

    @Schema(description = "로그인 타입", example = "아이디패스워드, 구글, 카카오")
    private LoginType loginType;
}
