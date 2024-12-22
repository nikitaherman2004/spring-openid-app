package com.open_id.backend.resolver;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

public interface TokenResolver {

    Optional<String> resolve(HttpServletRequest request);
}
