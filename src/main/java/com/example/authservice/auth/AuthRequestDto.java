package com.example.authservice.auth;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PACKAGE)
public class AuthRequestDto {

    LoginType loginType;

    Map<String, String> data;
}
