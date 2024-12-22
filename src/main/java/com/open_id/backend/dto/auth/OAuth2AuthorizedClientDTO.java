package com.open_id.backend.dto.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2AuthorizedClientDTO {

    private String principalName;

    private RedisAccessToken accessToken;

    private OAuth2RefreshTokenDTO refreshToken;

    @JsonIgnore
    private ClientRegistration clientRegistration;
}
