package com.example.authservice.auth;

import jakarta.persistence.Column;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class UserSearchStrategyProvider {

    private final Map<UserColumns,UserRepositoryMethodSelector> userSearchProviders;

    public UserSearchStrategyProvider(UserRepository userRepository) {
        this.userSearchProviders = Map.of(
                UserColumns.USERID, new UserByUserIdSearch(userRepository),
                UserColumns.USER_NAME, new UserByUserIdSearch(userRepository)
        );
    }
    public UserRepositoryMethodSelector getSearchUser(UserColumns columns) {
        return userSearchProviders.get(columns);
    }
}
