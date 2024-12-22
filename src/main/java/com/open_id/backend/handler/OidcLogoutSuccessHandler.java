package com.open_id.backend.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OidcLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    @Value("${post-logout-redirect-uri}")
    private String postLogoutRedirectUri;

    private final ClientRegistrationRepository clientRegistrationRepository;

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        String targetUrl = null;
        if (authentication instanceof OAuth2AuthenticationToken && authentication.getPrincipal() != null) {
            String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
            ClientRegistration clientRegistration = this.clientRegistrationRepository
                    .findByRegistrationId(registrationId);
            URI endSessionEndpoint = this.endSessionEndpoint(clientRegistration);
            if (endSessionEndpoint != null) {
                String postLogoutRedirectUri = postLogoutRedirectUri(request, clientRegistration);
                targetUrl = endpointUri(endSessionEndpoint, postLogoutRedirectUri);
            }
        }
        return (targetUrl != null) ? targetUrl : super.determineTargetUrl(request, response);
    }

    private URI endSessionEndpoint(ClientRegistration clientRegistration) {
        if (clientRegistration != null) {
            ClientRegistration.ProviderDetails providerDetails = clientRegistration.getProviderDetails();
            Map<String, Object> oidcProviderConfiguration = providerDetails.getConfigurationMetadata();

            Object endSessionEndpoint = oidcProviderConfiguration.get("revocation_endpoint");

            if (endSessionEndpoint != null) {
                return URI.create(endSessionEndpoint.toString());
            }
        }
        return null;
    }

    private String postLogoutRedirectUri(HttpServletRequest request, ClientRegistration clientRegistration) {
        if (this.postLogoutRedirectUri == null) {
            return null;
        }
        UriComponents uriComponents = UriComponentsBuilder
                .fromHttpUrl(UrlUtils.buildFullRequestUrl(request))
                .replacePath(request.getContextPath())
                .replaceQuery(null)
                .fragment(null)
                .build();

        Map<String, String> uriVariables = new HashMap<>();
        String scheme = uriComponents.getScheme();
        uriVariables.put("baseScheme", (scheme != null) ? scheme : "");
        uriVariables.put("baseUrl", uriComponents.toUriString());

        String host = uriComponents.getHost();
        uriVariables.put("baseHost", (host != null) ? host : "");

        String path = uriComponents.getPath();
        uriVariables.put("basePath", (path != null) ? path : "");

        int port = uriComponents.getPort();
        uriVariables.put("basePort", (port == -1) ? "" : ":" + port);

        uriVariables.put("registrationId", clientRegistration.getRegistrationId());

        return UriComponentsBuilder.fromUriString(this.postLogoutRedirectUri)
                .buildAndExpand(uriVariables)
                .toUriString();
    }

    private String endpointUri(URI endSessionEndpoint, String postLogoutRedirectUri) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(endSessionEndpoint);
        if (postLogoutRedirectUri != null) {
            builder.queryParam("post_logout_redirect_uri", postLogoutRedirectUri);
        }
        return builder.encode(StandardCharsets.UTF_8)
                .build()
                .toUriString();
    }
}
