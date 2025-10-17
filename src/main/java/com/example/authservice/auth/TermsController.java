package com.example.authservice.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping
@RestController
public class TermsController {
    private final TermsService termsService;

    @GetMapping("auth/all-terms")
    public ResponseEntity<List<TermsResponseDTO>> getAllTerms() {
        return ResponseEntity.ok(termsService.getAllTerms());
    }

//    @PostMapping()
//    public ResponseEntity<String> c
}
