package com.example.authservice.auth;


import com.example.authservice.utill.GlobalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping("/user/{userIdx}")
    public ResponseEntity<GlobalResponse<UserResponseDto>> user(@PathVariable  Long userIdx) {
        return GlobalResponse.success(UserResponseDto.toDto(userService.getUser(userIdx)));
    }

    @GetMapping("users/me")
    public ResponseEntity<GlobalResponse<UserResponseDto>> getUserMe() {

    return ResponseEntity.ok().body(null);
    }

    @GetMapping("user/list")
    public ResponseEntity<UserResponseDto> getUserList(@RequestParam String type,
                                                             @RequestParam String query
                                                             ) {
        UserResponseDto result =  userService.getUserList(type, query);

        return ResponseEntity.ok().body(result);
    }
}




