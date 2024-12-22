package com.open_id.backend.configuration.redis.serializer;

import com.open_id.backend.util.JsonParser;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientId;
import org.springframework.stereotype.Component;

@Component
public class Oauth2AuthorizedClientIdSerializer extends AbstractRedisSerializer<OAuth2AuthorizedClientId> {

    public Oauth2AuthorizedClientIdSerializer(JsonParser jsonParser) {
        super(jsonParser);
    }

    @Override
    public Class<OAuth2AuthorizedClientId> getTargetClass() {
        return OAuth2AuthorizedClientId.class;
    }
}
