package com.example.authservice.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RequiredArgsConstructor
public class UserByUserIdSearch implements UserRepositoryMethodSelector {
    private final UserRepository userRepository;

    @Override
    public User getSearchUser(String query) {
        return userRepository.findByUserId(query).orElseThrow(()-> new UsernameNotFoundException("Username not found"));
    }
}
