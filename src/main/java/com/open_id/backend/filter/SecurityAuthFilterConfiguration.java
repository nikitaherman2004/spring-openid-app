package com.open_id.backend.filter;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Component
public class SecurityAuthFilterConfiguration {

    private final String clientRegistrationId = "google";

    private final List<String> permittedUrls = List.of(
            "/oauth2/authorization/google",
            "/login/oauth2/code/google"
    );
}
