package com.open_id.backend.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OAuth2AuthorizedClientService {

    private final OAuth2AuthorizedClientRepository oauth2AuthorizedClientRepository;

    public OAuth2AuthorizedClientService(
            @Qualifier("redisAuthorizedClientRepository") OAuth2AuthorizedClientRepository oauth2AuthorizedClientRepository
    ) {
        this.oauth2AuthorizedClientRepository = oauth2AuthorizedClientRepository;
    }

    public Optional<OAuth2AuthorizedClient> getAuthorizedClient(OAuth2AuthenticationToken token, HttpServletRequest request) {
        OAuth2AuthorizedClient authorizedClient = oauth2AuthorizedClientRepository.loadAuthorizedClient(
                token.getAuthorizedClientRegistrationId(),
                token, request
        );

        return Optional.ofNullable(authorizedClient);
    }
}
