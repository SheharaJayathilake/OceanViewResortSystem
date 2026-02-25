package com.oceanview.exception;

/**
 * Base exception for all application-specific exceptions
 */
public class ApplicationException extends Exception {
    private static final long serialVersionUID = 1L;
    private final String errorCode;
    
    public ApplicationException(String message) {
        super(message);
        this.errorCode = "APP_ERROR";
    }
    
    public ApplicationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "APP_ERROR";
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
