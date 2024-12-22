package com.open_id.backend.dto.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RedisAccessToken {

    private Instant issuedAt;

    private Instant expiresAt;

    private String tokenValue;

    private Set<String> scopes;

    @JsonIgnore
    private OAuth2AccessToken.TokenType tokenType;
}
