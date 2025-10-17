package com.example.authservice.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserTermsServiceImpl implements UserTermsService {
    private final UserTermsRepository userTermsRepository;

    @Override
    public String userTermsCreate(UserTermsRequestDTO userTermsRequestDTO) {

        List<UserTerms> userTermsList = userTermsRequestDTO.getTerms().stream().map(
                dto -> {
                 UserTerms userTerms = new UserTerms();
                 userTerms.setTermId(dto.getId());
                 userTerms.setAgreed(dto.isAgreed());
                 return userTerms;
                })
                .collect(Collectors.toList());

        userTermsRepository.saveAll(userTermsList);
        return "success";
    }
}
