package com.flex.url_shortener.exception;

import static com.flex.url_shortener.common.ApplicationConstants.ExceptionMessage.BAD_CREDENTIALS;
import static com.flex.url_shortener.common.ApplicationConstants.ExceptionMessage.JWT_EXPIRED;
import static com.flex.url_shortener.common.ApplicationConstants.ExceptionMessage.JWT_VERIFICATION_FAILED;
import static com.flex.url_shortener.common.ApplicationConstants.ExceptionMessage.UNAUTHENTICATED;
import static com.flex.url_shortener.common.ApplicationConstants.ExceptionMessage.UNAUTHORIZED;
import static org.springframework.http.HttpHeaders.WWW_AUTHENTICATE;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ExceptionMessage> handleNoSuchElementException(
            HttpServletRequest request,
            NoSuchElementException ex) {
        log.error("Requested element does not exists:", ex);
        var errorMessage = new ExceptionMessage(HttpStatus.NOT_FOUND.value(),
                new Date(),
                ex.getMessage(),
                request.getRequestURI());

        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<ExceptionMessage> handleAuthenticationException(
            HttpServletRequest request,
            AuthenticationException exception) {
        log.error("Exception was thrown due authentication failure:", exception);

        final var message = ExceptionMessage.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .date(new Date())
                .description(BAD_CREDENTIALS)
                .url(request.getRequestURL().toString())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ExceptionMessage> handleJWTExpiredException(
            HttpServletRequest request,
            TokenExpiredException exception) {
        log.error("Exception was thrown due JWT expiration:", exception);

        var message = ExceptionMessage.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .date(new Date())
                .description(JWT_EXPIRED)
                .url(request.getRequestURL().toString())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header(WWW_AUTHENTICATE, "Bearer error=token_expired")
                .body(message);
    }

    @ExceptionHandler(JWTVerificationException.class)
    public ResponseEntity<ExceptionMessage> handleJWTVerificationException(
            HttpServletRequest request,
            JWTVerificationException exception) {
        log.error("Exception was thrown due JWT verification failure:", exception);

        var message = ExceptionMessage.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .date(new Date())
                .description(JWT_VERIFICATION_FAILED)
                .url(request.getRequestURL().toString())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header(WWW_AUTHENTICATE, "Bearer error=token_invalid")
                .body(message);
    }

    @ExceptionHandler({AuthenticationException.class, AuthenticationCookieMissing.class})
    public ResponseEntity<ExceptionMessage> handleAuthenticationException(
            HttpServletRequest request,
            RuntimeException exception) {
        log.error("Exception was thrown due authentication failure:", exception);

        final var message = ExceptionMessage.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .date(new Date())
                .description(UNAUTHENTICATED)
                .url(request.getRequestURL().toString())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionMessage> handleAccessDeniedException(
            HttpServletRequest request,
            AccessDeniedException exception) {
        log.error("Exception was thrown because access was denied:", exception);

        final var message = ExceptionMessage.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .date(new Date())
                .description(UNAUTHORIZED)
                .url(request.getRequestURL().toString())
                .build();

        return new ResponseEntity<>(message, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ValueDuplicationException.class)
    public ResponseEntity<ExceptionMessage> handleValueDuplicationException(
            HttpServletRequest request,
            ValueDuplicationException ex) {
        log.error("Value duplication encountered:", ex);
        var errorMessage = new ExceptionMessage(HttpStatus.BAD_REQUEST.value(),
                new Date(),
                ex.getMessage(),
                request.getRequestURI());

        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionMessage> handleDataIntegrityViolationException(
            HttpServletRequest request,
            DataIntegrityViolationException ex) {
        log.error("Data integrity violation occurred:", ex);
        String exceptionDescription = "Your request contains invalid or inconsistent data.";
        var errorMessage = new ExceptionMessage(HttpStatus.UNPROCESSABLE_ENTITY.value(),
                new Date(),
                exceptionDescription,
                request.getRequestURI());

        return new ResponseEntity<>(errorMessage, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionMessage> handleValidationErrors(final HttpServletRequest request,
                                                                   final MethodArgumentNotValidException ex) {
        log.error("Validation on an argument fails:", ex);

        var exceptionDescription = new StringBuilder();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            var defaultMessage = "%s: %s".formatted(error.getField(), error.getDefaultMessage());
            exceptionDescription.append(defaultMessage);
        }

        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            var defaultMessage = "%s: %s".formatted(error.getObjectName(), error.getDefaultMessage());
            exceptionDescription.append(defaultMessage);
        }
        var errorMessage = new ExceptionMessage(
                HttpStatus.BAD_REQUEST.value(), new Date(), exceptionDescription.toString(), request.getRequestURI());

        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionMessage> handleConstraintViolationException(
            HttpServletRequest request,
            ConstraintViolationException ex) {
        log.error("Constraint violation encountered:", ex);
        List<String> exceptions = ex.getConstraintViolations().stream().map(violation -> {
            var propertyName = ((PathImpl) violation.getPropertyPath()).getLeafNode().getName();

            return "%s : %s, value: %s".formatted(propertyName, violation.getMessage(), violation.getInvalidValue());
        }).toList();


        var errorMessage = new ExceptionMessage(
                HttpStatus.BAD_REQUEST.value(), new Date(), exceptions.toString(), request.getRequestURI());
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionMessage> handleAllExceptions(
            Exception exception,
            HttpServletRequest request) {
        log.error("Unhandled exception was thrown:", exception);

        var responseStatus =
                exception.getClass().getAnnotation(ResponseStatus.class);
        var status =
                responseStatus != null ? responseStatus.value() : HttpStatus.INTERNAL_SERVER_ERROR;
        var message = ExceptionMessage.builder()
                .status(status.value())
                .date(new Date())
                .description("An unexpected error occurred.")
                .url(request.getRequestURL().toString())
                .build();

        return new ResponseEntity<>(message, status);
    }


}