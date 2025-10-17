package com.example.authservice.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TermsServiceImpl implements TermsService {
    private final TermsRepository termsRepository;

    @Override
    public List<TermsResponseDTO> getAllTerms() {
        List<TermsResponseDTO> res = termsRepository.findAll().stream()
                .map(term -> new TermsResponseDTO(
                        term.getId(),
                        term.getTitle(),
                        term.getKey(),
                        term.getDescription(),
                        term.isRequired()
                ))
                .toList();
        return res;
    }
}
