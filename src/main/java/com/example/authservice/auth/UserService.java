package com.example.authservice.auth;

public interface UserService {
   User createUser(UserSignUpRequestDto requestDto);
   String checkUserId(String userId);
   User getUser(Long userIdx);
   User getUserIdx(String userId);
}
