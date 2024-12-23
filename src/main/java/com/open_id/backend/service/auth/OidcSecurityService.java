package com.open_id.backend.service.auth;

import com.open_id.backend.dto.response.SubjectRoleDto;
import com.open_id.backend.model.OAuth2UserAttributeAccessor;
import com.open_id.backend.model.OidcAuthenticationToken;
import com.open_id.backend.service.user.AppUserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.open_id.backend.util.CookieUtils.createCookie;

@Slf4j
@Service
@RequiredArgsConstructor
public class OidcSecurityService implements SessionAuthenticationStrategy {

    @Value("${cookie.access-session.name}")
    private String accessTokenCookieName;

    @Value("${cookie.access-session.max-age-in-seconds}")
    private Integer cookieMaxAge;

    private final AppUserService appUserService;

    private final RedisOidcIdTokenService accessTokenService;

    private final OAuth2UserAttributeAccessor attributeAccessor;

    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    public SubjectRoleDto getAuthedUserSubjectRole() {
        SecurityContext context = SecurityContextHolder.getContext();

        OidcAuthenticationToken authentication = (OidcAuthenticationToken) context.getAuthentication();

        return new SubjectRoleDto(
                authentication.getSub(), authentication.getRoleName()
        );
    }

    @Override
    public void onAuthentication(
            Authentication authentication, HttpServletRequest request, HttpServletResponse response
    ) throws SessionAuthenticationException {
        if (authentication instanceof OAuth2AuthenticationToken authenticationToken) {

            Optional<OAuth2AuthorizedClient> optional = oAuth2AuthorizedClientService.getAuthorizedClient(
                    authenticationToken, request
            );

            processAuthentication(optional, authenticationToken, response);
        } else {
            log.error(
                    "Only the available authentication implementation is supported - OAuth2AuthenticationToken," +
                            "but another implementation came {}"
                    , authentication
            );
            throw throwSessionAuthenticationException();
        }
    }

    private void processAuthentication(
            Optional<OAuth2AuthorizedClient> optional, OAuth2AuthenticationToken authenticationToken,
            HttpServletResponse response
    ) {
        if (optional.isPresent()) {
            OAuth2User principal = authenticationToken.getPrincipal();

            appUserService.createOrUpdateAppUser(principal);

            OAuth2AccessToken accessToken = optional.get().getAccessToken();

            processCookie(accessToken, response);
            accessTokenService.saveAccessToken(attributeAccessor.getSub(principal), accessToken);
        } else {
            log.error(
                    "Couldn't find an authorized client in the repository by name {}"
                    , authenticationToken.getName()
            );
            throw throwSessionAuthenticationException();
        }
    }

    private void processCookie(OAuth2AccessToken accessToken, HttpServletResponse response) {
        Cookie accessTokenCookie = createCookie(accessTokenCookieName, accessToken.getTokenValue(), cookieMaxAge);
        response.addCookie(accessTokenCookie);
    }

    private SessionAuthenticationException throwSessionAuthenticationException() {
        return new SessionAuthenticationException("Не удалось войти в приложение");
    }
}