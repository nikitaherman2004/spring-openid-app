package com.open_id.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApplicationException extends RuntimeException {

    protected String message;

    protected HttpStatus status;

    public ApplicationException(String message, HttpStatus status, Object... args) {
        this.status = status;
        this.message = String.format(message, args);
    }
}
