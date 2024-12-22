package com.open_id.backend.service.auth;

import com.open_id.backend.dto.response.SubjectRoleDto;
import com.open_id.backend.model.OAuth2UserAttributeAccessor;
import com.open_id.backend.model.OidcAuthenticationToken;
import com.open_id.backend.service.user.AppUserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.stereotype.Service;

import static com.open_id.backend.util.CookieUtils.createCookie;

@Slf4j
@Service
public class OidcSecurityService implements SessionAuthenticationStrategy {

    @Value("${cookie.access-session.name}")
    private String accessSessionCookie;

    @Value("${cookie.access-session.max-age-in-seconds}")
    private Integer accessCookieMaxAge;

    private final AppUserService appUserService;

    private final RedisOidcIdTokenService accessTokenService;

    private final OAuth2UserAttributeAccessor attributeAccessor;

    private final OAuth2AuthorizedClientRepository authorizedClientRepository;

    public OidcSecurityService(
            AppUserService appUserService,
            RedisOidcIdTokenService accessTokenService,
            OAuth2UserAttributeAccessor attributeAccessor,
            @Qualifier("redisAuthorizedClientRepository") OAuth2AuthorizedClientRepository authorizedClientRepository
    ) {
        this.attributeAccessor = attributeAccessor;
        this.accessTokenService = accessTokenService;
        this.appUserService = appUserService;
        this.authorizedClientRepository = authorizedClientRepository;
    }

    public SubjectRoleDto getAuthedUserSubjectRole() {
        SecurityContext context = SecurityContextHolder.getContext();

        OidcAuthenticationToken authentication = (OidcAuthenticationToken) context.getAuthentication();

        return new SubjectRoleDto(authentication.getSub(), authentication.getRoleName());
    }

    @Override
    public void onAuthentication(
            Authentication authentication, HttpServletRequest request, HttpServletResponse response
    ) throws SessionAuthenticationException {
        if (authentication instanceof OAuth2AuthenticationToken authenticationToken) {

            OAuth2AuthorizedClient authorizedClient = authorizedClientRepository.loadAuthorizedClient(
                    authenticationToken.getAuthorizedClientRegistrationId(),
                    authenticationToken, request
            );

            processAuthentication(authorizedClient, authenticationToken, response);
        } else {
            log.warn(
                    "Any implementations are not supported, OAuth2AuthenticationToken only supports, authentication {}"
                    , authentication
            );
            throw new SessionAuthenticationException("Не удалось авторизоваться");
        }
    }

    private void processAuthentication(
            OAuth2AuthorizedClient authorizedClient, OAuth2AuthenticationToken authenticationToken,
            HttpServletResponse response
    ) {
        if (authorizedClient != null) {
            OAuth2User principal = authenticationToken.getPrincipal();
            appUserService.createOrUpdateUserAppUser(principal);

            OAuth2AccessToken accessToken = authorizedClient.getAccessToken();

            Cookie accessSession = createCookie(accessSessionCookie, accessToken.getTokenValue(), accessCookieMaxAge);
            response.addCookie(accessSession);

            accessTokenService.saveAccessToken(attributeAccessor.getSub(principal), accessToken);
        } else {
            log.warn(
                    "Authorized client not found for {}, authorized client is null"
                    , authenticationToken.getName()
            );
            throw new SessionAuthenticationException("Не удалось авторизоваться");
        }
    }
}