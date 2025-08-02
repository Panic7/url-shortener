package com.flex.url_shortener.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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
        String errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        var errorMessage = new ExceptionMessage(
                HttpStatus.BAD_REQUEST.value(), new Date(), errors, request.getRequestURI());

        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionMessage> handleConstraintViolationException(
            HttpServletRequest request,
            ConstraintViolationException ex) {
        log.error("Constraint violation encountered:", ex);
        List<String> exceptions = ex.getConstraintViolations().stream().map(violation -> {
            var propertyName = ((PathImpl) violation.getPropertyPath()).getLeafNode().getName();

            return String.format(
                    "%s : %s, value: %s", propertyName, violation.getMessage(), violation.getInvalidValue()
            );
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