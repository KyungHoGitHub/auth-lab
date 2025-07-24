package com.example.authservice.auth;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryMethodSelector {
    User getSearchUser(String query);
}
