package com.open_id.backend.mapper;

import com.open_id.backend.dto.auth.RedisAccessToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

public class OAuth2AccessTokenMapper {

    public RedisAccessToken toDto(OAuth2AccessToken accessToken) {
        return new RedisAccessToken(
                accessToken.getIssuedAt(),
                accessToken.getExpiresAt(),
                accessToken.getTokenValue(),
                accessToken.getScopes(),
                OAuth2AccessToken.TokenType.BEARER
        );
    }

    public OAuth2AccessToken toEntity(RedisAccessToken redisAccessToken) {
        return new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                redisAccessToken.getTokenValue(),
                redisAccessToken.getIssuedAt(),
                redisAccessToken.getExpiresAt(),
                redisAccessToken.getScopes()
        );
    }
}
