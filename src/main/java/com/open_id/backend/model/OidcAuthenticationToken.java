package com.open_id.backend.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;

@Getter
@Setter
public class OidcAuthenticationToken extends OAuth2AuthenticationToken {

    private String sub;

    private String roleName;

    public OidcAuthenticationToken(
            OAuth2User principal,
            Collection<? extends GrantedAuthority> authorities,
            String authorizedClientRegistrationId,
            String sub, String roleName) {
        super(principal, authorities, authorizedClientRegistrationId);

        this.sub = sub;
        this.roleName = roleName;
    }
}
