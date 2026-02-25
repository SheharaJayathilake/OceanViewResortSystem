package com.oceanview.exception;

/**
 * Exception for authentication and authorization errors
 */
public class AuthenticationException extends ApplicationException {
    private static final long serialVersionUID = 1L;
    
    public AuthenticationException(String message) {
        super(message, "AUTH_ERROR");
    }
}
