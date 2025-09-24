package com.example.authservice.auth;

import com.example.authservice.config.JwtUtill;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final UserRepository userRepository;
    private final JwtUtill jwtUtill;
    private final AuthenticationManager authenticationManager;


    @Override
    public String getTempToken() {
        String userName = "admin";
        User user = userRepository.findByUsername(userName).orElseThrow(()-> new UsernameNotFoundException(userName));

        String accessToken = jwtUtill.generateAccessToken(user.getUsername(), user.getIdx(), user.getUserId(), user.getRole());
        return accessToken;
    }
}
