package com.open_id.backend.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SetAuthenticationAttributeLogoutHandler implements LogoutHandler {

    private static final String SAVE_AUTHENTICATION_ATTRIBUTE = "save_authentication_attribute";

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken) {
            request.setAttribute(SAVE_AUTHENTICATION_ATTRIBUTE, authentication);
        } else {
            log.warn(
                    "Failed to save authentication attribute, authentication is not OAuth2AuthenticationToken, authentication - {}",
                    authentication
            );
        }
    }
}
