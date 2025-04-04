package com.example.authservice.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class UserRequestDto {
    private String username;
    private String password;
}
