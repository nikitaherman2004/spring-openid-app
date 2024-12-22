package com.open_id.backend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2RefreshTokenDTO {

    private Instant issuedAt;

    private Instant expiresAt;

    private String tokenValue;
}
