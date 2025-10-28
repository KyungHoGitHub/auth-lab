package com.example.authservice.auth;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserTestImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RedisTemplate<String, UserInfo> userInfoRedisTemplate;

    private final UserSearchStrategyProvider userSearchStrategyProvider;

    @PostConstruct
    public void init() {
        try {
            syncAllUsersToRedis();
        }catch (Exception e) {
                System.out.println( e.getMessage());
                // 필요 시 대체 로직

        }
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
        UserColumns column = UserColumns.valueOf(searchBy.toUpperCase());

        User user = userSearchStrategyProvider.getSearchUser(column).getSearchUser(query);

        UserResponseDto response = new UserResponseDto(user.getIdx(),user.getUserId(),user.getUsername(),user.getRole(),user.getCreatedAt(),user.getEmail());

        return response;
    }

    @Override
    public List<UserResponseDto> getUsers() {
        List<User> users = userRepository.findAll();
        // stream() 메서드 사용해서 리스트 데이터 변환
         List<UserResponseDto> response = userRepository.findAll().stream()
                 .map(user -> new UserResponseDto(user.getIdx(),user.getUserId(),user.getUsername(),user.getRole(),user.getCreatedAt(),user.getEmail()))
                 .toList();

        return response;
    }

    @Override
    public User createOauthUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
