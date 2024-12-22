package com.open_id.backend.configuration.redis.serializer;

import com.open_id.backend.dto.auth.OAuth2AuthorizedClientDTO;
import com.open_id.backend.util.JsonParser;
import org.springframework.stereotype.Component;

@Component
public class Oauth2AuthorizedClientRedisSerializer extends AbstractRedisSerializer<OAuth2AuthorizedClientDTO> {

    public Oauth2AuthorizedClientRedisSerializer(JsonParser jsonParser) {
        super(jsonParser);
    }

    @Override
    public Class<OAuth2AuthorizedClientDTO> getTargetClass() {
        return OAuth2AuthorizedClientDTO.class;
    }
}
