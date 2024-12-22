package com.open_id.backend.configuration.redis.serializer;

import com.open_id.backend.util.JsonParser;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public abstract class AbstractRedisSerializer<T> implements RedisSerializer<T>  {

    private final JsonParser jsonParser;

    public AbstractRedisSerializer(JsonParser jsonParser) {
        this.jsonParser = jsonParser;
    }

    @Override
    public byte[] serialize(T value) throws SerializationException {
        String json = jsonParser.toJson(value);

        return json.getBytes();
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        String json = new String(bytes);

        return jsonParser.fromJson(json, getTargetClass());
    }

    public abstract Class<T> getTargetClass();
}