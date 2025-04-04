package com.example.authservice.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignUpRequestDto {

    private String userId;
    private String username;
    private String password;
}
