package com.open_id.backend.repository;

import com.open_id.backend.dto.auth.RedisAccessToken;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RedisAccessTokenRepository {

    private ValueOperations<String, RedisAccessToken> valueOperations;

    private final RedisTemplate<String, RedisAccessToken> redisTemplate;

    @PostConstruct
    private void init() {
        this.valueOperations = redisTemplate.opsForValue();
    }

    public Optional<RedisAccessToken> getAccessToken(String key) {
        RedisAccessToken accessToken = valueOperations.get(key);

        return Optional.ofNullable(accessToken);
    }

    public void saveAccessToken(String key, RedisAccessToken accessToken) {
        valueOperations.set(key, accessToken);
    }
}
