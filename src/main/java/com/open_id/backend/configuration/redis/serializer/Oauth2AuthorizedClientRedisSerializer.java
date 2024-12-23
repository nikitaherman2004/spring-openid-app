package com.open_id.backend.configuration.redis.serializer;

import com.open_id.backend.dto.auth.RedisOAuth2AuthorizedClient;
import com.open_id.backend.util.JsonParser;
import org.springframework.stereotype.Component;

@Component
public class Oauth2AuthorizedClientRedisSerializer extends AbstractRedisSerializer<RedisOAuth2AuthorizedClient> {

    public Oauth2AuthorizedClientRedisSerializer(JsonParser jsonParser) {
        super(jsonParser);
    }

    @Override
    public Class<RedisOAuth2AuthorizedClient> getTargetClass() {
        return RedisOAuth2AuthorizedClient.class;
    }
}
