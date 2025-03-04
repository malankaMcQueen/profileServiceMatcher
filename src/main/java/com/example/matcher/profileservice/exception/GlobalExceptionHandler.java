package com.example.matcher.profileservice.exception;

import com.example.matcher.profileservice.aspect.AspectAnnotation;
import com.example.matcher.profileservice.controllers.ProfileController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;


@Component
@RestControllerAdvice
public class GlobalExceptionHandler {
//    @AspectAnnotation
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ErrorMessage> userAlreadyExistException(final UserAlreadyExistException ex,
                                                            final WebRequest request) {
        logger.error("Event=EXCEPTION_HANDLER, Path={}, Msg='User already exist: {}'", request.getDescription(false), ex.getMessage());

        ErrorMessage message = new ErrorMessage(
                HttpStatus.CONFLICT.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorMessage> invalidCredentialsException(final InvalidCredentialsException ex,
                                                                  final WebRequest request) {
        logger.error("Event=EXCEPTION_HANDLER, Path={}, Msg='Invalid credentials: {}'", request.getDescription(false), ex.getMessage());

        ErrorMessage message = new ErrorMessage(
                HttpStatus.UNAUTHORIZED.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(message, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorMessage> tokenExpiredException(final TokenExpiredException ex,
                                                                    final WebRequest request) {
        logger.error("Event=EXCEPTION_HANDLER, Path={}, Msg='Invalid credentials: {}'", request.getDescription(false), ex.getMessage());

        ErrorMessage message = new ErrorMessage(
                HttpStatus.UNAUTHORIZED.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(message, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorMessage> resourceNotFoundException(final ResourceNotFoundException ex,
                                                                  final WebRequest request) {
        logger.error("Event=EXCEPTION_HANDLER, Path={}, Msg='Resource not found: {}'", request.getDescription(false), ex.getMessage());
        ErrorMessage message = new ErrorMessage(
                HttpStatus.NOT_FOUND.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

//    @AspectAnnotation
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorMessage> badRequestException(final BadRequestException ex,
                                                            final WebRequest request) {
        logger.error("Event=EXCEPTION_HANDLER, Path={}, Msg='Bad request: {}'", request.getDescription(false), ex.getMessage());

        ErrorMessage message = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

//    @AspectAnnotation
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> globalExceptionHandler(final Exception ex,
                                                               final WebRequest request) {
        logger.error("Event=EXCEPTION_HANDLER, Path={}, Msg='Undefined exception: {}'", request.getDescription(false), ex.getMessage());
        ErrorMessage message = new ErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
//    @AspectAnnotation
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessage> httpMessageNotReadableException(final HttpMessageNotReadableException ex,
                                                                        final WebRequest request) {
        logger.error("Event=EXCEPTION_HANDLER, Path={}, Msg='Invalid request body', Error={}",
                request.getDescription(false), ex.getMostSpecificCause().getMessage());

        ErrorMessage message = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                "Invalid request body: " + ex.getMostSpecificCause().getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
}
