package com.oceanview.exception;

/**
 * Exception for business logic validation errors
 */
public class BusinessException extends ApplicationException {
    private static final long serialVersionUID = 1L;
    
    public BusinessException(String message) {
        super(message, "BUSINESS_ERROR");
    }
    
    public BusinessException(String message, String errorCode) {
        super(message, errorCode);
    }
}
