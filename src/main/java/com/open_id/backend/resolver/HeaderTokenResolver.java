package com.open_id.backend.resolver;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Component
public class HeaderTokenResolver implements TokenResolver {

    private static final String BEARER = "Bearer";

    @Override
    public Optional<String> resolve(HttpServletRequest request) {
        String bearerHeaderValue = request.getHeader(BEARER);

        if (StringUtils.hasText(bearerHeaderValue)) {
            return Optional.of(bearerHeaderValue);
        }

        return Optional.empty();
    }
}
