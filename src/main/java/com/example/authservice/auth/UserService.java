package com.example.authservice.auth;

import java.util.List;

public interface UserService {
   User createUser(UserSignUpRequestDto requestDto);
   String checkUserId(String userId);
   User getUser(Long userIdx);
   User getUserIdx(String userId);

   UserResponseDto getUserList(String searchBy, String query);

   List<UserResponseDto> getUsers();
}
