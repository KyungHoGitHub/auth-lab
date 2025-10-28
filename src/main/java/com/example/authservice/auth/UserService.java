package com.example.authservice.auth;

import java.util.List;
import java.util.Optional;

public interface UserService {
   User createUser(UserSignUpRequestDto requestDto);
   String checkUserId(String userId);
   User getUser(Long userIdx);
   User getUserIdx(String userId);

   UserResponseDto getUserList(String searchBy, String query);

   List<UserResponseDto> getUsers();

   User createOauthUser(User user);

   Optional<User> findByEmail(String email);
}
