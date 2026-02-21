package com.oceanview.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for password hashing and security operations
 * Implements industry-standard cryptographic practices
 */
public class PasswordUtil {
    private static final Logger LOGGER = Logger.getLogger(PasswordUtil.class.getName());
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    
    // Private constructor to prevent instantiation
    private PasswordUtil() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    /**
     * Generate cryptographically secure salt
     * @return Base64 encoded salt
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * Hash password with salt using SHA-256
     * @param password Plain text password
     * @param salt Salt for hashing
     * @return Hashed password
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update(salt.getBytes());
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "Hashing algorithm not found", e);
            throw new RuntimeException("Password hashing failed", e);
        }
    }
    
    /**
     * Simple MD5 hash for backward compatibility (not recommended for production)
     * @param password Plain text password
     * @return MD5 hashed password
     */
    public static String md5Hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "MD5 algorithm not found", e);
            throw new RuntimeException("MD5 hashing failed", e);
        }
    }
    
    /**
     * Verify password against stored hash
     * @param password Plain text password to verify
     * @param storedHash Stored password hash
     * @param salt Salt used for original hash
     * @return true if password matches
     */
    public static boolean verifyPassword(String password, String storedHash, String salt) {
        String hashedInput = hashPassword(password, salt);
        return hashedInput.equals(storedHash);
    }
    
    /**
     * Validate password strength
     * @param password Password to validate
     * @return true if password meets strength requirements
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
        
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
}
