package com.open_id.backend.repository;

import com.open_id.backend.dto.auth.OAuth2AuthorizedClientDTO;
import com.open_id.backend.dto.auth.OAuth2RefreshTokenDTO;
import com.open_id.backend.dto.auth.RedisAccessToken;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientId;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisAuthorizedClientRepository implements OAuth2AuthorizedClientRepository {

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final RedisTemplate<OAuth2AuthorizedClientId, OAuth2AuthorizedClientDTO> redisTemplate;

    private ValueOperations<OAuth2AuthorizedClientId, OAuth2AuthorizedClientDTO> valueOperations;

    @PostConstruct
    private void init() {
        this.valueOperations = redisTemplate.opsForValue();
    }

    @Override
    public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(
            String clientRegistrationId,
            Authentication principal,
            HttpServletRequest request
    ) {
        Assert.hasText(clientRegistrationId, "clientRegistrationId cannot be empty");
        Assert.hasText(principal.getName(), "principalName cannot be empty");

        ClientRegistration registration = clientRegistrationRepository.findByRegistrationId(clientRegistrationId);
        OAuth2AuthorizedClientDTO authorizedClientDTO = valueOperations.get(getKey(clientRegistrationId, principal));

        return (T) mapToAuthorizedClient(authorizedClientDTO, registration);
    }

    private OAuth2AuthorizedClient mapToAuthorizedClient(
            OAuth2AuthorizedClientDTO authorizedClientDTO, ClientRegistration registration
    ) {
        if (authorizedClientDTO == null || registration == null) {
            log.error(
                    "The registration client {} or the authorized client {} were not found.",
                    authorizedClientDTO, registration
            );
            return null;
        }
        RedisAccessToken accessTokenDto = authorizedClientDTO.getAccessToken();
        OAuth2RefreshTokenDTO refreshTokenDto = authorizedClientDTO.getRefreshToken();

        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                accessTokenDto.getTokenValue(),
                accessTokenDto.getIssuedAt(),
                accessTokenDto.getExpiresAt()
        );

        OAuth2RefreshToken refreshToken = refreshTokenDto != null
                ? new OAuth2RefreshToken(
                        refreshTokenDto.getTokenValue(), refreshTokenDto.getIssuedAt(), refreshTokenDto.getExpiresAt()
                  )
                : null;

        return new OAuth2AuthorizedClient(
                registration, authorizedClientDTO.getPrincipalName(), accessToken, refreshToken
        );
    }

    @Override
    public void saveAuthorizedClient(
            OAuth2AuthorizedClient authorizedClient, Authentication principal,
            HttpServletRequest request, HttpServletResponse response
    ) {
        Assert.notNull(principal, "principal cannot be null");
        Assert.notNull(authorizedClient, "authorizedClient cannot be null");

        String clientRegistrationId = authorizedClient.getClientRegistration().getRegistrationId();

        valueOperations.set(
                getKey(clientRegistrationId, principal),
                mapToAuthorizedClientDto(authorizedClient, principal)
        );
    }

    private OAuth2AuthorizedClientDTO mapToAuthorizedClientDto(
            OAuth2AuthorizedClient authorizedClient, Authentication principal
    ) {
        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();

        RedisAccessToken accessTokenDTO = new RedisAccessToken(
                accessToken.getIssuedAt(),
                accessToken.getExpiresAt(),
                accessToken.getTokenValue(),
                accessToken.getScopes(),
                OAuth2AccessToken.TokenType.BEARER
        );
        OAuth2RefreshToken refreshToken = authorizedClient.getRefreshToken();

        OAuth2RefreshTokenDTO refreshTokenDTO = refreshToken != null
                ? new OAuth2RefreshTokenDTO(
                        refreshToken.getIssuedAt(), refreshToken.getExpiresAt(), refreshToken.getTokenValue()
                  )
                : null;

        return new OAuth2AuthorizedClientDTO(
                principal.getName(), accessTokenDTO, refreshTokenDTO, authorizedClient.getClientRegistration()
        );
    }

    @Override
    public void removeAuthorizedClient(
            String clientRegistrationId, Authentication principal,
            HttpServletRequest request, HttpServletResponse response
    ) {
        Assert.hasText(clientRegistrationId, "clientRegistrationId cannot be empty");
        Assert.hasText(principal.getName(), "principalName cannot be empty");

        ClientRegistration registration = clientRegistrationRepository.findByRegistrationId(clientRegistrationId);

        if (registration != null) {
            log.warn(
                    "The authorized client could not be deleted from the cache because " +
                            "the registration client was not found {}",
                    registration
            );
            redisTemplate.delete(getKey(clientRegistrationId, principal));
        }
    }

    private OAuth2AuthorizedClientId getKey(String clientRegistrationId, Authentication principal) {
        String principalName = principal.getName();

        return new OAuth2AuthorizedClientId(clientRegistrationId, principalName);
    }
}