package com.open_id.backend.util;

import com.open_id.backend.resolver.TokenResolver;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenResolverService {

    private final List<TokenResolver> resolvers;

    public Optional<String> resolve(HttpServletRequest request) {
        for (TokenResolver resolver : resolvers) {
            Optional<String> optionalToken = resolver.resolve(request);

            if (optionalToken.isPresent()) {
                return optionalToken;
            }
        }
        return Optional.empty();
    }
}
