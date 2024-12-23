package com.open_id.backend.repository;

import com.open_id.backend.dto.auth.RedisOAuth2AuthorizedClient;
import com.open_id.backend.mapper.OAuth2AuthorizedClientMapper;
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
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisAuthorizedClientRepository implements OAuth2AuthorizedClientRepository {

    private final OAuth2AuthorizedClientMapper authorizedClientMapper = new OAuth2AuthorizedClientMapper();

    private ValueOperations<OAuth2AuthorizedClientId, RedisOAuth2AuthorizedClient> valueOperations;

    private final ClientRegistrationRepository clientRegistrationRepository;

    private final RedisTemplate<OAuth2AuthorizedClientId, RedisOAuth2AuthorizedClient> redisTemplate;

    @PostConstruct
    private void init() {
        this.valueOperations = redisTemplate.opsForValue();
    }

    @Override
    public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(
            String clientRegistrationId,
            Authentication authentication,
            HttpServletRequest request
    ) {
        ClientRegistration registration = clientRegistrationRepository.findByRegistrationId(clientRegistrationId);

        RedisOAuth2AuthorizedClient redisOAuth2AuthorizedClient = valueOperations.get(
                getKey(clientRegistrationId, authentication)
        );

        return (T) authorizedClientMapper.mapToOAuth2AuthorizedClient(redisOAuth2AuthorizedClient, registration);
    }

    @Override
    public void saveAuthorizedClient(
            OAuth2AuthorizedClient authorizedClient, Authentication principal,
            HttpServletRequest request, HttpServletResponse response
    ) {
        String clientRegistrationId = authorizedClient.getClientRegistration().getRegistrationId();

        valueOperations.set(
                getKey(clientRegistrationId, principal),
                authorizedClientMapper.mapToRedisOAuth2AuthorizedClient(authorizedClient, principal)
        );
    }

    @Override
    public void removeAuthorizedClient(
            String clientRegistrationId, Authentication authentication,
            HttpServletRequest request, HttpServletResponse response
    ) {
        ClientRegistration registration = clientRegistrationRepository.findByRegistrationId(clientRegistrationId);

        if (registration != null) {
            redisTemplate.delete(getKey(clientRegistrationId, authentication));
        } else {
            log.warn(
                    "The authorized client could not be deleted from the cache because " +
                            "the registration client was not found"
            );
        }
    }

    private OAuth2AuthorizedClientId getKey(String clientRegistrationId, Authentication principal) {
        String principalName = principal.getName();

        return new OAuth2AuthorizedClientId(clientRegistrationId, principalName);
    }
}