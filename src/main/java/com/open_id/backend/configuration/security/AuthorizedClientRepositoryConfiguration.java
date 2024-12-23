package com.open_id.backend.configuration.security;

import com.open_id.backend.dto.auth.RedisOAuth2AuthorizedClient;
import com.open_id.backend.repository.RedisAuthorizedClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientId;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Configuration
@RequiredArgsConstructor
public class AuthorizedClientRepositoryConfiguration {

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final RedisTemplate<OAuth2AuthorizedClientId, RedisOAuth2AuthorizedClient> redisTemplate;

    @Bean("redisAuthorizedClientRepository")
    public RedisAuthorizedClientRepository configureClientRepository() {
        return new RedisAuthorizedClientRepository(clientRegistrationRepository, redisTemplate);
    }
}
