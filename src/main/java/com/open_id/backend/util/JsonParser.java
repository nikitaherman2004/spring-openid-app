package com.open_id.backend.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.open_id.backend.exception.ApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JsonParser {

    private final ObjectMapper objectMapper;

    public JsonParser(@Qualifier("objectMapperWithJsr310Support") ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException exception) {
            log.error("Json deserialization error {}", exception.getMessage());

            throw throwApplicationException();
        }
    }

    public String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException exception) {
            log.error("Json serialization error, message {}", exception.getMessage());

            throw throwApplicationException();
        }
    }

    private ApplicationException throwApplicationException() {
        return new ApplicationException("Произошла ошибка на стороне сервера", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}