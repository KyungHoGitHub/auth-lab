package com.example.authservice.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping
@RestController
public class UserTermsController {
    private final UserTermsService userTermsService;

    @PostMapping("user-terms/{userEmail}")
    public ResponseEntity<AuthResponseDto> createUserTerms(@PathVariable String userEmail, @RequestBody UserTermsRequestDTO requestDTO) {
            userTermsService.userTermsCreate(requestDTO,userEmail);

            AuthResponseDto responseDTO = new AuthResponseDto();
            responseDTO.setAccessToken("eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhYmFiIiwiaWF0IjoxNzUwOTA0MTc5LCJleHAiOjE3NTA5MTEzNzl9.f9gWmn-uLYDz1vvBUJNC6q-8N2XwzADi_CALec1eGlEyjXttDK8JJZpKUTJUd3sO");
        return ResponseEntity.ok(responseDTO);
    }
}
