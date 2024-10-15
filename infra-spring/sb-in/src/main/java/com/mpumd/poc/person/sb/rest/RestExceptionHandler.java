package com.mpumd.poc.person.sb.rest;

import com.mpumd.poc.person.application.exception.PersonAlreadyExistException;
import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
class RestExceptionHandler {

    // https://www.baeldung.com/rest-api-error-handling-best-practices
    @Builder
    private record Error(
            String type,
            String title,
            int status,
            String detail,
            String instance
    ) {
    }

    @ExceptionHandler(PersonAlreadyExistException.class)
    ResponseEntity<Error> handlePersonAlreadyExistException(PersonAlreadyExistException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Error.builder()
                        .type(ex.getClass().getSimpleName())
                        .title(ex.getMessage())
                        .status(HttpStatus.CONFLICT.value())
                        .build());
    }
}