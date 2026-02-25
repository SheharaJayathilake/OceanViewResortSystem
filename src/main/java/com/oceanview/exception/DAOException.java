package com.oceanview.exception;

/**
 * Exception for data access layer errors
 */
public class DAOException extends ApplicationException {
    private static final long serialVersionUID = 1L;
    
    public DAOException(String message) {
        super(message, "DAO_ERROR");
    }
    
    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }
}
