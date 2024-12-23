package com.open_id.backend.configuration.redis;

import com.open_id.backend.configuration.redis.serializer.OAuth2AccessTokenSerializer;
import com.open_id.backend.configuration.redis.serializer.Oauth2AuthorizedClientIdSerializer;
import com.open_id.backend.configuration.redis.serializer.Oauth2AuthorizedClientRedisSerializer;
import com.open_id.backend.dto.auth.RedisOAuth2AuthorizedClient;
import com.open_id.backend.dto.auth.RedisAccessToken;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientId;

@Configuration
@RequiredArgsConstructor
public class RedisTemplateConfiguration {

    @Value("${spring.data.redis.host}")
    private String hostname;

    @Value("${spring.data.redis.username}")
    private String username;

    @Value("${spring.data.redis.password}")
    private String password;

    @Value("${spring.data.redis.port}")
    private Integer port;

    private JedisConnectionFactory jedisConnectionFactory;

    private final OAuth2AccessTokenSerializer accessTokenSerializer;

    private final Oauth2AuthorizedClientIdSerializer oauth2AuthorizedClientIdSerializer;

    private final Oauth2AuthorizedClientRedisSerializer oauth2AuthorizedClientRedisSerializer;

    @PostConstruct
    public void init() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();

        configuration.setPort(port);
        configuration.setHostName(hostname);
        configuration.setUsername(username);
        configuration.setPassword(password);

        this.jedisConnectionFactory = new JedisConnectionFactory(configuration);

        jedisConnectionFactory.start();
    }

    @Bean
    public RedisTemplate<OAuth2AuthorizedClientId, RedisOAuth2AuthorizedClient> configureAuthClientsRedisTemplate() {
        RedisTemplate<OAuth2AuthorizedClientId, RedisOAuth2AuthorizedClient> redisTemplate = new RedisTemplate<>();

        redisTemplate.setKeySerializer(oauth2AuthorizedClientIdSerializer);
        redisTemplate.setValueSerializer(oauth2AuthorizedClientRedisSerializer);
        redisTemplate.setHashKeySerializer(oauth2AuthorizedClientIdSerializer);
        redisTemplate.setHashValueSerializer(oauth2AuthorizedClientRedisSerializer);

        redisTemplate.setConnectionFactory(jedisConnectionFactory);

        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, RedisAccessToken> configureAccessTokenRedisTemplate() {
        RedisTemplate<String, RedisAccessToken> redisTemplate = new RedisTemplate<>();

        redisTemplate.setValueSerializer(accessTokenSerializer);

        redisTemplate.setConnectionFactory(jedisConnectionFactory);

        return redisTemplate;
    }

    @PreDestroy
    public void destroy() {
        jedisConnectionFactory.stop();
    }
}
