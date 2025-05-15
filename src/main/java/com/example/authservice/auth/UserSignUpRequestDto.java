package com.example.authservice.auth;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSignUpRequestDto {

    String userId;

    String userName;

    String password;

    String email;
}
