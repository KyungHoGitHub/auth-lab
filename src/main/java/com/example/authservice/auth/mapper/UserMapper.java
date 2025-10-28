package com.example.authservice.auth.mapper;

import com.example.authservice.auth.User;
import com.example.authservice.auth.UserRequestDto;
import com.example.authservice.auth.UserSignUpRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    User toEntityUser(UserSignUpRequestDto userSignUpRequestDto);

}
