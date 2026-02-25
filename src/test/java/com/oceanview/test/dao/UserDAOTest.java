package com.oceanview.test.dao;

import com.oceanview.dao.impl.UserDAOImpl;
import com.oceanview.model.User;
import com.oceanview.model.User.UserRole;
import com.oceanview.exception.DAOException;
import com.oceanview.util.PasswordUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Optional;

/**
 * Unit tests for UserDAOImpl
 * Tests CRUD operations and business logic
 */
public class UserDAOTest {
    
    private UserDAOImpl userDAO;
    private User testUser;
    
    @Before
    public void setUp() {
        userDAO = new UserDAOImpl();
        
        testUser = new User();
        testUser.setUsername("testuser_" + System.currentTimeMillis());
        testUser.setPasswordHash(PasswordUtil.md5Hash("password123"));
        testUser.setFullName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setRole(UserRole.RECEPTIONIST);
        testUser.setActive(true);
    }
    
    @After
    public void tearDown() {
        // Clean up test data
        if (testUser != null && testUser.getUserId() != null) {
            try {
                userDAO.delete(testUser.getUserId());
            } catch (DAOException e) {
                // Ignore cleanup errors
            }
        }
    }
    
    @Test
    public void testCreateUser() throws DAOException {
        User createdUser = userDAO.create(testUser);
        
        assertNotNull("Created user should not be null", createdUser);
        assertNotNull("User ID should be generated", createdUser.getUserId());
        assertEquals("Username should match", testUser.getUsername(), createdUser.getUsername());
        assertEquals("Email should match", testUser.getEmail(), createdUser.getEmail());
        
        System.out.println("✓ testCreateUser passed");
    }
    
    @Test
    public void testFindUserById() throws DAOException {
        User createdUser = userDAO.create(testUser);
        
        Optional<User> foundUser = userDAO.findById(createdUser.getUserId());
        
        assertTrue("User should be found", foundUser.isPresent());
        assertEquals("Username should match", testUser.getUsername(), foundUser.get().getUsername());
        
        System.out.println("✓ testFindUserById passed");
    }
    
    @Test
    public void testFindUserByUsername() throws DAOException {
        User createdUser = userDAO.create(testUser);
        
        Optional<User> foundUser = userDAO.findByUsername(testUser.getUsername());
        
        assertTrue("User should be found", foundUser.isPresent());
        assertEquals("User ID should match", createdUser.getUserId(), foundUser.get().getUserId());
        
        System.out.println("✓ testFindUserByUsername passed");
    }
    
    @Test
    public void testUpdateUser() throws DAOException {
        User createdUser = userDAO.create(testUser);
        
        createdUser.setFullName("Updated Name");
        createdUser.setEmail("updated@example.com");
        
        boolean updated = userDAO.update(createdUser);
        
        assertTrue("Update should succeed", updated);
        
        Optional<User> updatedUser = userDAO.findById(createdUser.getUserId());
        assertTrue("User should exist", updatedUser.isPresent());
        assertEquals("Name should be updated", "Updated Name", updatedUser.get().getFullName());
        
        System.out.println("✓ testUpdateUser passed");
    }
    
    @Test
    public void testDeleteUser() throws DAOException {
        User createdUser = userDAO.create(testUser);
        Integer userId = createdUser.getUserId();
        
        boolean deleted = userDAO.delete(userId);
        
        assertTrue("Delete should succeed", deleted);
        
        Optional<User> deletedUser = userDAO.findById(userId);
        assertFalse("User should not exist after deletion", deletedUser.isPresent());
        
        testUser = null; // Prevent cleanup attempt
        System.out.println("✓ testDeleteUser passed");
    }
    
    @Test
    public void testFindAllUsers() throws DAOException {
        userDAO.create(testUser);
        
        List<User> users = userDAO.findAll();
        
        assertNotNull("User list should not be null", users);
        assertTrue("User list should not be empty", users.size() > 0);
        
        System.out.println("✓ testFindAllUsers passed - Found " + users.size() + " users");
    }
    
    @Test
    public void testCountUsers() throws DAOException {
        long countBefore = userDAO.count();
        
        userDAO.create(testUser);
        
        long countAfter = userDAO.count();
        
        assertEquals("Count should increase by 1", countBefore + 1, countAfter);
        
        System.out.println("✓ testCountUsers passed");
    }
    
    @Test(expected = DAOException.class)
    public void testCreateUserWithNullData() throws DAOException {
        userDAO.create(null);
        
        System.out.println("✓ testCreateUserWithNullData passed");
    }
    
    @Test
    public void testFindNonExistentUser() throws DAOException {
        Optional<User> user = userDAO.findById(999999);
        
        assertFalse("Non-existent user should not be found", user.isPresent());
        
        System.out.println("✓ testFindNonExistentUser passed");
    }
}
