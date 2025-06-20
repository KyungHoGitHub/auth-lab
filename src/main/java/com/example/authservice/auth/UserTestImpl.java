package com.example.authservice.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserTestImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public User createUser(UserSignUpRequestDto requestDto) {
        User users = User.builder().userId(requestDto.getUserId()).username(requestDto.getUserName()).password(passwordEncoder.encode(requestDto.getPassword())).email(requestDto.getEmail()).build();
        return userRepository.save(users);
    }

    @Override
    public String checkUserId(String userId) {
        boolean isUserIdExists = userRepository.existsByUserId(userId);
        if (isUserIdExists) {
            throw new UsernameNotFoundException("Username " + userId + " already exists");
        }
        return "";
    }

    @Override
    public User getUser(Long userIdx) {
     return userRepository.findById(userIdx).orElseThrow(()-> new UsernameNotFoundException(String.valueOf(userIdx)));
    }

    @Override
    public User getUserIdx(String userId) {
        return userRepository.findByUserId(userId).orElseThrow(()-> new UsernameNotFoundException(String.valueOf(userId)));
    }
}
