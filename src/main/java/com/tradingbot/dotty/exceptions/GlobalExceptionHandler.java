package com.tradingbot.dotty.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

// TODO Exceptions Handling \ Retries
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Auth0Exceptions.BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleBadRequestException(Auth0Exceptions.BadRequestException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), ex.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = Auth0Exceptions.UnauthorizedAccessException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(Auth0Exceptions.UnauthorizedAccessException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), ex.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = Auth0Exceptions.ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleForbiddenException(Auth0Exceptions.ForbiddenException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), ex.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = Auth0Exceptions.TooManyRequestsException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public ResponseEntity<ErrorResponse> handleTooManyRequestsException(Auth0Exceptions.TooManyRequestsException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), ex.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.TOO_MANY_REQUESTS);
    }

}
