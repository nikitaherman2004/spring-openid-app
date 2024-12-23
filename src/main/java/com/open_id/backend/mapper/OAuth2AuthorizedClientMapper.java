package com.open_id.backend.mapper;

import com.open_id.backend.dto.auth.RedisAccessToken;
import com.open_id.backend.dto.auth.RedisOAuth2AuthorizedClient;
import com.open_id.backend.dto.auth.RedisRefreshToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;

@Slf4j
public class OAuth2AuthorizedClientMapper {

    public OAuth2AuthorizedClient mapToOAuth2AuthorizedClient(
            RedisOAuth2AuthorizedClient redisOAuth2AuthorizedClient, ClientRegistration clientRegistration
    ) {
        if (redisOAuth2AuthorizedClient == null || clientRegistration == null) {
            log.warn(
                    "The registration client {} or the authorized client {} were not found.",
                    redisOAuth2AuthorizedClient, clientRegistration
            );
            return null;
        }
        RedisAccessToken redisAccessToken = redisOAuth2AuthorizedClient.getAccessToken();
        RedisRefreshToken redisRefreshToken = redisOAuth2AuthorizedClient.getRefreshToken();

        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                redisAccessToken.getTokenValue(),
                redisAccessToken.getIssuedAt(),
                redisAccessToken.getExpiresAt()
        );

        OAuth2RefreshToken refreshToken = redisRefreshToken != null
                ? new OAuth2RefreshToken(
                redisRefreshToken.getTokenValue(), redisRefreshToken.getIssuedAt(), redisRefreshToken.getExpiresAt()
        )
                : null;

        return new OAuth2AuthorizedClient(
                clientRegistration, redisOAuth2AuthorizedClient.getPrincipalName(), accessToken, refreshToken
        );
    }

    public RedisOAuth2AuthorizedClient mapToRedisOAuth2AuthorizedClient(
            OAuth2AuthorizedClient authorizedClient, Authentication authentication
    ) {
        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();

        RedisAccessToken redisAccessToken = new RedisAccessToken(
                accessToken.getIssuedAt(),
                accessToken.getExpiresAt(),
                accessToken.getTokenValue(),
                accessToken.getScopes(),
                OAuth2AccessToken.TokenType.BEARER
        );
        OAuth2RefreshToken refreshToken = authorizedClient.getRefreshToken();

        RedisRefreshToken redisRefreshToken = refreshToken != null
                ? new RedisRefreshToken(
                refreshToken.getIssuedAt(), refreshToken.getExpiresAt(), refreshToken.getTokenValue()
        )
                : null;

        return new RedisOAuth2AuthorizedClient(
                authentication.getName(), redisAccessToken, redisRefreshToken, authorizedClient.getClientRegistration()
        );
    }
}