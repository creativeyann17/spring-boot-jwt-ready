package com.creativeyann17.springbootjwtready.services;

import com.creativeyann17.springbootjwtready.models.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.Optional;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandlerService {
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ErrorResponse> responseStatusException(ResponseStatusException e) {
    log.warn(e.getMessage());
    final HttpStatus status = Optional.ofNullable(e.getStatus()).orElse(HttpStatus.INTERNAL_SERVER_ERROR);
    final ErrorResponse errorDto = ErrorResponse.builder()
        .timestamp(new Date())
        .message(e.getReason())
        .error(status.getReasonPhrase())
        .status(e.getRawStatusCode())
        .build();
    return new ResponseEntity<>(errorDto, e.getStatus());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> exception(Exception e) {
    log.error(e.getMessage(), e);
    final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    final ErrorResponse errorDto = ErrorResponse.builder()
        .timestamp(new Date())
        .message(e.getMessage())
        .error(status.getReasonPhrase())
        .status(status.value())
        .build();
    return new ResponseEntity<>(errorDto, status);
  }
}
