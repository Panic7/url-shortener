package com.flex.url_shortener.exception;

/**
 * Exception thrown when a value that is expected to be unique is duplicated
 */
public class ValueDuplicationException extends RuntimeException {

    public ValueDuplicationException(String message) {
        super(message);
    }
}
