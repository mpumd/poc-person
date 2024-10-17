package com.mpumd.poc.person.sb.rest;

import com.mpumd.poc.person.application.exception.PersonAlreadyExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @see https://datatracker.ietf.org/doc/html/rfc9457
 * @see https://www.baeldung.com/rest-api-error-handling-best-practices
 */

@ControllerAdvice
class RestExceptionHandler {

    @ExceptionHandler(PersonAlreadyExistException.class)
    ResponseEntity<ProblemDetail> handlePersonAlreadyExistException(PersonAlreadyExistException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(fillProblemDetailResponse(ex, HttpStatus.CONFLICT));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ProblemDetail> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(fillProblemDetailResponse(ex, HttpStatus.BAD_REQUEST));
    }

    private static ProblemDetail fillProblemDetailResponse(RuntimeException ex, HttpStatus httpStatus) {
        ProblemDetail body = ProblemDetail.forStatus(httpStatus);
        body.setTitle(ex.getClass().getSimpleName());
        body.setDetail(ex.getMessage());
        return body;
    }
}