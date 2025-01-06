package com.open_id.backend.filter;

import com.open_id.backend.client.GoogleRestClient;
import com.open_id.backend.model.GoogleIdToken;
import com.open_id.backend.model.OidcAuthenticationToken;
import com.open_id.backend.service.auth.RedisOidcIdTokenService;
import com.open_id.backend.service.user.UserRoleService;
import com.open_id.backend.util.TokenResolverService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.*;

@Slf4j
@Component
public class SecurityAuthFilter extends OncePerRequestFilter {

    private final DefaultOAuth2UserService oAuth2UserService = new DefaultOAuth2UserService();

    private final UserRoleService userRoleService;

    private final GoogleRestClient googleRestClient;

    private final TokenResolverService tokenResolverService;

    private final RedisOidcIdTokenService accessTokenService;

    private final OrRequestMatcher permittedUrlsRequestMatcher;

    private final SecurityAuthFilterConfiguration filterConfiguration;

    private final ClientRegistrationRepository clientRegistrationRepository;

    public SecurityAuthFilter(UserRoleService userRoleService,
                              GoogleRestClient googleRestClient,
                              TokenResolverService tokenResolverService,
                              RedisOidcIdTokenService accessTokenService,
                              SecurityAuthFilterConfiguration filterConfiguration,
                              ClientRegistrationRepository clientRegistrationRepository) {
        this.userRoleService = userRoleService;
        this.googleRestClient = googleRestClient;
        this.accessTokenService = accessTokenService;
        this.filterConfiguration = filterConfiguration;
        this.tokenResolverService = tokenResolverService;
        this.clientRegistrationRepository = clientRegistrationRepository;

        List<AntPathRequestMatcher> requestMatchers = new ArrayList<>();

        filterConfiguration.getPermittedUrls()
                .forEach((permittedUri) -> requestMatchers.add(new AntPathRequestMatcher(permittedUri)));

        this.permittedUrlsRequestMatcher = new OrRequestMatcher(requestMatchers.toArray(new AntPathRequestMatcher[0]));
    }

    @Override
    @SneakyThrows
    protected void doFilterInternal(
            @NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) {
        if (permittedUrlsRequestMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            handleAuthorization(request);

            filterChain.doFilter(request, response);
        } catch (AuthenticationException | AccessDeniedException exception) {
            log.error(
                    "An authorization error has occurred, and an error message has been sent {}",
                    exception.getMessage()
            );
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Не удалось авторизоваться. Повторите попытку");
        }
    }

    private void handleAuthorization(HttpServletRequest request) {
        String accessTokenValue = resolveAccessToken(request);

        Authentication authenticationResult = verifyAccessTokenAndAuthenticate(accessTokenValue);

        SecurityContextHolder.getContext().setAuthentication(authenticationResult);
    }

    private Authentication verifyAccessTokenAndAuthenticate(String accessTokenValue) {
        GoogleIdToken googleIdToken = googleRestClient.verifyGoogleAccessToken(accessTokenValue);

        OAuth2AccessToken cachedAccessToken = accessTokenService.getAccessToken(
                googleIdToken.getSub(), accessTokenValue
        );

        Authentication authentication = createAuthentication(cachedAccessToken, googleIdToken.getSub());

        if (cachedAccessToken.getTokenValue().equals(accessTokenValue)) {
            return authentication;
        } else {
            log.warn(
                    "The access token from the cache ({}) does not match the access token from the request ({})",
                    accessTokenValue, cachedAccessToken.getTokenValue()
            );
            throw new AccessDeniedException("Access tokens don't match");
        }
    }

    private Authentication createAuthentication(OAuth2AccessToken accessToken, String sub) {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(
                filterConfiguration.getClientRegistrationId()
        );

        if (clientRegistration == null) {
            log.error(
                    "The client registration for the provider could not be found in the repository, " +
                            "the client registration ID {}",
                    filterConfiguration.getClientRegistrationId()
            );
            throw new OAuth2AuthenticationException("Client registration not found");
        }
        OAuth2UserRequest userRequest = new OAuth2UserRequest(clientRegistration, accessToken);

        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

        String roleName = userRoleService.findUserRoleBySub(sub);

        return new OidcAuthenticationToken(
                oAuth2User,
                createUserGrantedAuthorities(sub),
                clientRegistration.getRegistrationId(),
                sub,
                roleName
        );
    }

    private Set<? extends GrantedAuthority> createUserGrantedAuthorities(String roleName) {
        Set<SimpleGrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(roleName));

        return grantedAuthorities;
    }

    private String resolveAccessToken(HttpServletRequest request) {
        Optional<String> optional = tokenResolverService.resolve(request);

        if (optional.isEmpty()) {
            log.warn("Couldn't get access token from request");

            throw new AccessDeniedException("Access token not found");
        }

        return optional.get();
    }
}
