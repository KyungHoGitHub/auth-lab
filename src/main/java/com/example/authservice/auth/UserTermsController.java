package com.example.authservice.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping
@RestController
public class UserTermsController {
    private final UserTermsService userTermsService;

    @PostMapping("user-terms")
    public ResponseEntity<String> createUserTerms(@RequestBody UserTermsRequestDTO requestDTO) {
            userTermsService.userTermsCreate(requestDTO);

        return ResponseEntity.ok("Success");
    }
}
