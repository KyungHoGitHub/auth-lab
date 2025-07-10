package com.example.authservice.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@RequiredArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class UserInfo implements Serializable {
    private  Long userIdx;
    private String userId;
    private String userName;
}
