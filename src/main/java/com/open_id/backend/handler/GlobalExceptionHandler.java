package com.open_id.backend.handler;


import com.open_id.backend.dto.response.ErrorDto;
import com.open_id.backend.exception.ApplicationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<?> handleApplicationException(ApplicationException exception) {
        ErrorDto errorDto = new ErrorDto(exception.getMessage());

        return new ResponseEntity<>(errorDto, exception.getStatus());
    }
}
