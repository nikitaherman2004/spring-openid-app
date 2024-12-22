package com.open_id.backend.resolver;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CookieTokenResolver implements TokenResolver {

    @Value("${cookie.access-session.name}")
    private String accessSessionCookie;

    @Override
    public Optional<String> resolve(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(accessSessionCookie)) {
                return Optional.of(cookie.getValue());
            }
        }

        return Optional.empty();
    }
}
