package com.open_id.backend.configuration.redis.serializer;

import com.open_id.backend.dto.auth.RedisAccessToken;
import com.open_id.backend.util.JsonParser;
import org.springframework.stereotype.Component;

@Component
public class OAuth2AccessTokenSerializer extends AbstractRedisSerializer<RedisAccessToken> {

    public OAuth2AccessTokenSerializer(JsonParser jsonParser) {
        super(jsonParser);
    }

    @Override
    public Class<RedisAccessToken> getTargetClass() {
        return RedisAccessToken.class;
    }
}
