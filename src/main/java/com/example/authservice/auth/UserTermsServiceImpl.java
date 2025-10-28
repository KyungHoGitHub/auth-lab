package com.example.authservice.auth;

import com.example.authservice.config.JwtUtill;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserTermsServiceImpl implements UserTermsService {
    private final UserTermsRepository userTermsRepository;
    private final UserTermsRegistrationService userTermsRegistrationService;
    private final JwtUtill jwtUtill;

    @Override
    public String userTermsCreate(UserTermsRequestDTO userTermsRequestDTO, String userEmail) {


        User res = userTermsRegistrationService.registerTermsAgreementsUser(userEmail);

        List<UserTerms> userTermsList = userTermsRequestDTO.getTerms().stream().map(
                        dto -> {
                            UserTerms userTerms = new UserTerms();
                            userTerms.setTermId(dto.getId());
                            userTerms.setAgreed(dto.isAgreed());
                            return userTerms;
                        })
                .collect(Collectors.toList());

        userTermsRepository.saveAll(userTermsList);

        String accessToken =  jwtUtill.generateAccessToken(res.username,res.idx, res.userId,"user");


        return accessToken;
    }

}
