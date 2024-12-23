package com.open_id.backend.resolver;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CookieTokenResolver implements TokenResolver {

    @Value("${cookie.access-session.name}")
    private String accessTokenCookieName;

    @Override
    public Optional<String> resolve(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return Optional.empty();
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(accessTokenCookieName)) {
                return Optional.of(cookie.getValue());
            }
        }

        return Optional.empty();
    }
}
