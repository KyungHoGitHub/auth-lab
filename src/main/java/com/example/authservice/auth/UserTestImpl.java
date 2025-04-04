package com.example.authservice.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserTestImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public User createUser(UserSignUpRequestDto requestDto) {
        User users = User.builder().userId(requestDto.getUserId()).username(requestDto.getUsername()).password(passwordEncoder.encode(requestDto.getPassword())).build();

        return userRepository.save(users);
    }
}
