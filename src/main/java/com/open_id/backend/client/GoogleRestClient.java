package com.open_id.backend.client;

import com.open_id.backend.model.GoogleIdToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleRestClient {

    private static final String ACCESS_TOKEN_PARAM = "access_token";

    @Value("${spring.security.oauth2.client.registration.google.token_verifier_uri}")
    private String tokenVerifierUri;

    private final RestTemplate restTemplate = new RestTemplate();

    public GoogleIdToken verifyGoogleAccessToken(String tokenValue) {
        URI verifyAccessTokenUri = buildVerifierAccessTokenURI(tokenValue);

        RequestEntity<Void> requestEntity = new RequestEntity<>(
                HttpMethod.GET, verifyAccessTokenUri
        );

        return handleHttpClientException(() -> restTemplate.exchange(requestEntity, GoogleIdToken.class)
                .getBody());
    }

    private URI buildVerifierAccessTokenURI(String tokenValue) {
        return UriComponentsBuilder.fromUriString(tokenVerifierUri)
                .queryParam(ACCESS_TOKEN_PARAM, tokenValue)
                .build()
                .toUri();
    }

    private GoogleIdToken handleHttpClientException(Supplier<GoogleIdToken> fetcher) {
        try {
            return fetcher.get();
        } catch (RestClientException exception) {
            log.warn(
                    "The Google provider threw an exception when sending a request for access token validation, " +
                            "error message {}",
                    exception.getMessage()
            );
            if (exception instanceof HttpClientErrorException.BadRequest badRequest) {
                log.warn(
                        "Bad request status, error message {}",
                        badRequest.getMessage()
                );
            }
            throw new AccessDeniedException("Google access token is invalid");
        }
    }
}