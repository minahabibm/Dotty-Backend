package com.tradingbot.dotty.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

// TODO Exceptions Handling \ Retries
@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle InvalidException (400)
    @ExceptionHandler(value = {
            Auth0Exceptions.BadRequestException.class,
            EntityOperationExceptions.InvalidException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleBadRequestException(RuntimeException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle UnauthorizedAccessException (401)
    @ExceptionHandler(value = Auth0Exceptions.UnauthorizedAccessException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(Auth0Exceptions.UnauthorizedAccessException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.getReasonPhrase(), ex.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    // Handle ForbiddenException (403)
    @ExceptionHandler(value = Auth0Exceptions.ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleForbiddenException(Auth0Exceptions.ForbiddenException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.getReasonPhrase(), ex.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    // Handle NotFoundException (404)
    @ExceptionHandler(value = EntityOperationExceptions.NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleNotFoundException(EntityOperationExceptions.NotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), ex.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // Handle AlreadyExistsException (409)
    @ExceptionHandler(value = EntityOperationExceptions.AlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorResponse> handleConflictException(EntityOperationExceptions.AlreadyExistsException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT.getReasonPhrase(), ex.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    // Handle TooManyRequestsException (429)
    @ExceptionHandler(value = Auth0Exceptions.TooManyRequestsException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public ResponseEntity<ErrorResponse> handleTooManyRequestsException(Auth0Exceptions.TooManyRequestsException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase(), ex.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.TOO_MANY_REQUESTS);
    }

    // Handle SaveException (500)
    @ExceptionHandler(value = EntityOperationExceptions.SaveException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleSaveException(EntityOperationExceptions.SaveException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), ex.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Handle UpdateException (500)
    @ExceptionHandler(value = EntityOperationExceptions.UpdateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleUpdateException(EntityOperationExceptions.UpdateException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), ex.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Handle DeleteException (500)
    @ExceptionHandler(value = EntityOperationExceptions.DeleteException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleDeleteException(EntityOperationExceptions.DeleteException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), ex.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Handle generic Exception (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), ex.getLocalizedMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
