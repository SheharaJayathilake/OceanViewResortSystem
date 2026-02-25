package com.oceanview.test.util;

import com.oceanview.util.ValidationUtil;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDate;

/**
 * Unit tests for ValidationUtil
 */
public class ValidationUtilTest {
    
    @Test
    public void testValidEmail() {
        assertTrue("Should accept valid email", 
            ValidationUtil.isValidEmail("test@example.com"));
        assertTrue("Should accept email with subdomain", 
            ValidationUtil.isValidEmail("user@mail.example.com"));
        
        System.out.println("✓ testValidEmail passed");
    }
    
    @Test
    public void testInvalidEmail() {
        assertFalse("Should reject email without @", 
            ValidationUtil.isValidEmail("testexample.com"));
        assertFalse("Should reject email without domain", 
            ValidationUtil.isValidEmail("test@"));
        assertFalse("Should reject null", 
            ValidationUtil.isValidEmail(null));
        
        System.out.println("✓ testInvalidEmail passed");
    }
    
    @Test
    public void testValidPhone() {
        assertTrue("Should accept 10 digit phone", 
            ValidationUtil.isValidPhone("0771234567"));
        assertTrue("Should accept phone with country code", 
            ValidationUtil.isValidPhone("+94771234567"));
        
        System.out.println("✓ testValidPhone passed");
    }
    
    @Test
    public void testInvalidPhone() {
        assertFalse("Should reject short phone", 
            ValidationUtil.isValidPhone("123"));
        assertFalse("Should reject phone with letters", 
            ValidationUtil.isValidPhone("077ABC1234"));
        assertFalse("Should reject null", 
            ValidationUtil.isValidPhone(null));
        
        System.out.println("✓ testInvalidPhone passed");
    }
    
    @Test
    public void testValidDateRange() {
        LocalDate start = LocalDate.of(2026, 3, 1);
        LocalDate end = LocalDate.of(2026, 3, 5);
        
        assertTrue("Should accept valid range", 
            ValidationUtil.isValidDateRange(start, end));
        assertTrue("Should accept same day", 
            ValidationUtil.isValidDateRange(start, start));
        
        System.out.println("✓ testValidDateRange passed");
    }
    
    @Test
    public void testInvalidDateRange() {
        LocalDate start = LocalDate.of(2026, 3, 5);
        LocalDate end = LocalDate.of(2026, 3, 1);
        
        assertFalse("Should reject end before start", 
            ValidationUtil.isValidDateRange(start, end));
        assertFalse("Should reject null dates", 
            ValidationUtil.isValidDateRange(null, null));
        
        System.out.println("✓ testInvalidDateRange passed");
    }
    
    @Test
    public void testIsNotEmpty() {
        assertTrue("Should accept non-empty string", 
            ValidationUtil.isNotEmpty("Hello"));
        assertFalse("Should reject empty string", 
            ValidationUtil.isNotEmpty(""));
        assertFalse("Should reject whitespace only", 
            ValidationUtil.isNotEmpty("   "));
        assertFalse("Should reject null", 
            ValidationUtil.isNotEmpty(null));
        
        System.out.println("✓ testIsNotEmpty passed");
    }
}
