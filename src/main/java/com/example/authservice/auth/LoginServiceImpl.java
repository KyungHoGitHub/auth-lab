package com.example.authservice.auth;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LoginServiceImpl implements LoginService {
    private final Map<LoginType, LoginStrategy> strategies;

    public LoginServiceImpl(List<LoginStrategy> strategyList){
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(LoginStrategy::getLoginType, Function.identity()));
    }

    @Override
    public AuthResponseDto login(LoginRequestDto loginRequestDto) {
        LoginStrategy strategy = strategies.get(loginRequestDto.getLoginType());
        if(strategy == null){
            throw new IllegalArgumentException("Unknown login type: " + loginRequestDto.getLoginType());
        }
        return strategy.login(loginRequestDto);
    }
}
