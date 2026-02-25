package com.oceanview.test.util;

import com.oceanview.util.PasswordUtil;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for PasswordUtil
 */
public class PasswordUtilTest {
    
    @Test
    public void testMD5Hash() {
        String password = "password123";
        String hash = PasswordUtil.md5Hash(password);
        
        assertNotNull("Hash should not be null", hash);
        assertEquals("Hash length should be 32", 32, hash.length());
        
        // Same password should produce same hash
        String hash2 = PasswordUtil.md5Hash(password);
        assertEquals("Same password should produce same hash", hash, hash2);
        
        System.out.println("✓ testMD5Hash passed");
    }
    
    @Test
    public void testGenerateSalt() {
        String salt1 = PasswordUtil.generateSalt();
        String salt2 = PasswordUtil.generateSalt();
        
        assertNotNull("Salt 1 should not be null", salt1);
        assertNotNull("Salt 2 should not be null", salt2);
        assertNotEquals("Salts should be unique", salt1, salt2);
        
        System.out.println("✓ testGenerateSalt passed");
    }
    
    @Test
    public void testHashPasswordWithSalt() {
        String password = "myPassword123";
        String salt = PasswordUtil.generateSalt();
        
        String hash1 = PasswordUtil.hashPassword(password, salt);
        String hash2 = PasswordUtil.hashPassword(password, salt);
        
        assertNotNull("Hash should not be null", hash1);
        assertEquals("Same password and salt should produce same hash", hash1, hash2);
        
        System.out.println("✓ testHashPasswordWithSalt passed");
    }
    
    @Test
    public void testVerifyPassword() {
        String password = "testPassword";
        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hashPassword(password, salt);
        
        assertTrue("Should verify correct password", 
            PasswordUtil.verifyPassword(password, hash, salt));
        assertFalse("Should reject incorrect password", 
            PasswordUtil.verifyPassword("wrongPassword", hash, salt));
        
        System.out.println("✓ testVerifyPassword passed");
    }
    
    @Test
    public void testStrongPassword() {
        assertTrue("Should accept strong password", 
            PasswordUtil.isStrongPassword("MyP@ssw0rd"));
        assertTrue("Should accept strong password with special chars", 
            PasswordUtil.isStrongPassword("Abc123!@#"));
        
        assertFalse("Should reject short password", 
            PasswordUtil.isStrongPassword("Ab1!"));
        assertFalse("Should reject password without uppercase", 
            PasswordUtil.isStrongPassword("abc123!@#"));
        assertFalse("Should reject password without lowercase", 
            PasswordUtil.isStrongPassword("ABC123!@#"));
        assertFalse("Should reject password without digit", 
            PasswordUtil.isStrongPassword("AbcDef!@#"));
        assertFalse("Should reject password without special char", 
            PasswordUtil.isStrongPassword("AbcDef123"));
        
        System.out.println("✓ testStrongPassword passed");
    }
}
