package com.open_id.backend.configuration.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer.INSTANCE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

@Configuration
public class ObjectMapperConfiguration {

    @Bean("objectMapperWithJsr310Support")
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        javaTimeModule.addSerializer(ZonedDateTime.class, INSTANCE);
        javaTimeModule.addSerializer(Instant.class, InstantSerializer.INSTANCE);
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(ISO_LOCAL_DATE_TIME));

        objectMapper.registerModule(javaTimeModule);

        return objectMapper;
    }
}
