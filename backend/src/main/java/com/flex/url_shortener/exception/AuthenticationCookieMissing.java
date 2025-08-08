package com.flex.url_shortener.exception;

/**
 * Exception thrown when the authentication cookie is missing from the request.
 * This typically indicates that the user is not authenticated or the session has expired.
 */
public class AuthenticationCookieMissing extends RuntimeException {

    public AuthenticationCookieMissing() {
        super("Authentication cookie is missing");
    }
}
