package com.open_id.backend.service.auth;

import com.open_id.backend.dto.auth.RedisAccessToken;
import com.open_id.backend.mapper.OAuth2AccessTokenMapper;
import com.open_id.backend.repository.RedisAccessTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisOidcIdTokenService {

    private final OAuth2AccessTokenMapper oAuth2AccessTokenMapper = new OAuth2AccessTokenMapper();

    private final RedisAccessTokenRepository accessTokenRepository;

    public Optional<RedisAccessToken> getAccessToken(String key) {
        return accessTokenRepository.getAccessToken(key);
    }

    public void saveAccessToken(String sub, OAuth2AccessToken accessToken) {
        String key = getKey(sub, accessToken.getTokenValue());

        RedisAccessToken redisAccessToken = oAuth2AccessTokenMapper.toDto(accessToken);

        accessTokenRepository.saveAccessToken(key, redisAccessToken);
    }

    public OAuth2AccessToken getAccessToken(String sub, String tokenValue) {
        String key = getKey(sub, tokenValue);
        Optional<RedisAccessToken> optional = getAccessToken(key);

        if (optional.isPresent()) {
            RedisAccessToken redisAccessToken = optional.get();

            return oAuth2AccessTokenMapper.toEntity(redisAccessToken);
        } else {
            throw new AccessDeniedException(
                    String.format(
                            "Couldn't find access token by prefix %s and token value %s",
                            sub,
                            tokenValue
            ));
        }
    }

    private String getKey(String subPrefix, String tokenValue) {
        return subPrefix + ":" + tokenValue;
    }
}
