package com.oceanview.util;

import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * Centralized validation utility for input validation
 * Implements comprehensive validation logic
 */
public class ValidationUtil {
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[+]?[0-9]{10,15}$");
    private static final Pattern ALPHA_PATTERN = 
        Pattern.compile("^[a-zA-Z\\s]+$");
    
    private ValidationUtil() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
    
    public static boolean isValidName(String name) {
        return name != null && name.trim().length() >= 2 && 
               ALPHA_PATTERN.matcher(name.trim()).matches();
    }
    
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    public static boolean isValidDateRange(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            return false;
        }
        return !end.isBefore(start);
    }
    
    public static boolean isFutureDate(LocalDate date) {
        return date != null && !date.isBefore(LocalDate.now());
    }
    
    public static boolean isPositiveNumber(Number number) {
        return number != null && number.doubleValue() > 0;
    }
}
