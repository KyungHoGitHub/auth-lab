package com.example.authservice.auth;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserTestImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RedisTemplate<String, UserInfo> userInfoRedisTemplate;


    @PostConstruct
    public void init() {
        syncAllUsersToRedis();
    }

    public void syncAllUsersToRedis(){
        List<User> users = userRepository.findAll();
        for (User user : users) {
            UserInfo userInfo = new UserInfo(user.getIdx(),user.getUserId(),user.getUsername());
            userInfoRedisTemplate.opsForValue().set("user:"+ user.getUserId(), userInfo,24, TimeUnit.HOURS);
        }
    }
    @Override
    public User createUser(UserSignUpRequestDto requestDto) {
        User users = User.builder()
                .userId(requestDto.getUserId())
                .username(requestDto.getUserName())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .email(requestDto.getEmail())
                .role("user")
                .build();
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

    @Override
    public UserResponseDto getUserList(String searchBy, String query) {
        User user = userRepository.findByUsername("abab").orElseThrow(()-> new UsernameNotFoundException("Username   not found"));
        UserResponseDto response = new UserResponseDto(user.getIdx(),user.getUserId(),user.getUsername(),user.getRole(),user.getCreatedAt(),user.getEmail());
        return response;
    }
}
