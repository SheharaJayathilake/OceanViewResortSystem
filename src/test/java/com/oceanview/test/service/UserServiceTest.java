package com.oceanview.test.service;

import com.oceanview.service.UserService;
import com.oceanview.dao.impl.UserDAOImpl;
import com.oceanview.model.User;
import com.oceanview.model.User.UserRole;
import com.oceanview.exception.BusinessException;
import com.oceanview.exception.DAOException;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Unit tests for UserService
 * Uses DAO stubs to isolate business logic from database
 */
public class UserServiceTest {

    private UserService userService;
    private User testUser;
    private static final Integer ADMIN_USER_ID = 999;

    /**
     * Stub DAO that simulates database operations in-memory
     */
    private class UserDAOStub extends UserDAOImpl {
        private List<User> users = new ArrayList<>();
        private int nextId = 100;

        public UserDAOStub() {
            // Pre-populate with an admin user
            User admin = new User();
            admin.setUserId(ADMIN_USER_ID);
            admin.setUsername("admin");
            admin.setPasswordHash("e10adc3949ba59abbe56e057f20f883e");
            admin.setFullName("System Admin");
            admin.setEmail("admin@oceanview.com");
            admin.setRole(UserRole.ADMIN);
            admin.setActive(true);
            users.add(admin);
        }

        @Override
        public User create(User user) throws DAOException {
            user.setUserId(nextId++);
            users.add(user);
            return user;
        }

        @Override
        public Optional<User> findById(Integer id) throws DAOException {
            return users.stream()
                    .filter(u -> u.getUserId().equals(id))
                    .findFirst();
        }

        @Override
        public Optional<User> findByUsername(String username) throws DAOException {
            return users.stream()
                    .filter(u -> u.getUsername().equals(username))
                    .findFirst();
        }

        @Override
        public Optional<User> findByEmail(String email) throws DAOException {
            return users.stream()
                    .filter(u -> u.getEmail() != null && u.getEmail().equals(email))
                    .findFirst();
        }

        @Override
        public List<User> findAll() throws DAOException {
            return new ArrayList<>(users);
        }

        @Override
        public boolean update(User user) throws DAOException {
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getUserId().equals(user.getUserId())) {
                    users.set(i, user);
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean delete(Integer id) throws DAOException {
            return users.removeIf(u -> u.getUserId().equals(id));
        }

        @Override
        public long countByRole(UserRole role) throws DAOException {
            return users.stream()
                    .filter(u -> u.getRole() == role && u.isActive())
                    .count();
        }

        @Override
        public boolean updatePassword(Integer userId, String newPasswordHash) throws DAOException {
            Optional<User> user = findById(userId);
            if (user.isPresent()) {
                user.get().setPasswordHash(newPasswordHash);
                return true;
            }
            return false;
        }

        @Override
        public List<User> searchUsers(String searchTerm) throws DAOException {
            List<User> result = new ArrayList<>();
            for (User u : users) {
                if (u.getUsername().contains(searchTerm) || u.getFullName().contains(searchTerm)) {
                    result.add(u);
                }
            }
            return result;
        }
    }

    @Before
    public void setUp() {
        UserDAOStub stubDAO = new UserDAOStub();
        userService = new UserService(stubDAO);

        testUser = new User();
        testUser.setUsername("newuser");
        testUser.setFullName("New User");
        testUser.setEmail("newuser@oceanview.com");
        testUser.setRole(UserRole.RECEPTIONIST);
        testUser.setActive(true);
    }

    // ===== CREATE TESTS =====

    @Test
    public void testCreateValidUser() throws BusinessException {
        User result = userService.createUser(testUser, "Test@1234");
        assertNotNull("Created user should not be null", result);
        assertNotNull("User ID should be generated", result.getUserId());
        assertEquals("Username should match", "newuser", result.getUsername());
        System.out.println("✓ testCreateValidUser passed");
    }

    @Test(expected = BusinessException.class)
    public void testCreateUserDuplicateUsername() throws BusinessException {
        testUser.setUsername("admin");
        userService.createUser(testUser, "Test@1234");
    }

    @Test(expected = BusinessException.class)
    public void testCreateUserWeakPassword() throws BusinessException {
        userService.createUser(testUser, "weak");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUserEmptyUsername() throws BusinessException {
        testUser.setUsername("");
        userService.createUser(testUser, "Test@1234");
    }

    @Test(expected = BusinessException.class)
    public void testCreateUserInvalidUsername() throws BusinessException {
        testUser.setUsername("ab");
        userService.createUser(testUser, "Test@1234");
    }

    @Test(expected = BusinessException.class)
    public void testCreateUserNullUser() throws BusinessException {
        userService.createUser(null, "Test@1234");
    }

    // ===== UPDATE TESTS =====

    @Test
    public void testUpdateUser() throws BusinessException {
        User created = userService.createUser(testUser, "Test@1234");
        created.setFullName("Updated Name");
        boolean updated = userService.updateUser(created, ADMIN_USER_ID);
        assertTrue("Update should succeed", updated);
        System.out.println("✓ testUpdateUser passed");
    }

    @Test(expected = BusinessException.class)
    public void testSelfDeactivation() throws BusinessException {
        User admin = userService.findUserById(ADMIN_USER_ID);
        admin.setActive(false);
        userService.updateUser(admin, ADMIN_USER_ID);
    }

    @Test(expected = BusinessException.class)
    public void testSelfRoleChange() throws BusinessException {
        User admin = userService.findUserById(ADMIN_USER_ID);
        // Create a copy so we don't modify the same reference the stub DAO holds
        User modifiedAdmin = new User();
        modifiedAdmin.setUserId(admin.getUserId());
        modifiedAdmin.setUsername(admin.getUsername());
        modifiedAdmin.setRole(UserRole.RECEPTIONIST); // Changed role
        modifiedAdmin.setActive(admin.isActive());

        userService.updateUser(modifiedAdmin, ADMIN_USER_ID);
    }

    // ===== DELETE TESTS =====

    @Test(expected = BusinessException.class)
    public void testDeleteSelfPrevented() throws BusinessException {
        userService.deleteUser(ADMIN_USER_ID, ADMIN_USER_ID);
    }

    @Test
    public void testDeleteOtherUser() throws BusinessException {
        User created = userService.createUser(testUser, "Test@1234");
        boolean deleted = userService.deleteUser(created.getUserId(), ADMIN_USER_ID);
        assertTrue("Delete should succeed", deleted);
        System.out.println("✓ testDeleteOtherUser passed");
    }

    @Test(expected = BusinessException.class)
    public void testDeleteLastAdmin() throws BusinessException {
        // Create another user (non-admin) to be the "performer"
        User performer = new User();
        performer.setUsername("manager1");
        performer.setFullName("Manager One");
        performer.setEmail("mgr@oceanview.com");
        performer.setRole(UserRole.MANAGER);
        performer.setActive(true);
        User createdPerformer = userService.createUser(performer, "Test@1234");

        // Try to delete the only admin — should fail
        userService.deleteUser(ADMIN_USER_ID, createdPerformer.getUserId());
    }

    // ===== PASSWORD TESTS =====

    @Test
    public void testResetPassword() throws BusinessException {
        User created = userService.createUser(testUser, "Test@1234");
        userService.resetPassword(created.getUserId(), "NewPass@1234");
        System.out.println("✓ testResetPassword passed");
    }

    @Test(expected = BusinessException.class)
    public void testResetPasswordWeak() throws BusinessException {
        User created = userService.createUser(testUser, "Test@1234");
        userService.resetPassword(created.getUserId(), "weak");
    }

    // ===== TOGGLE STATUS TESTS =====

    @Test
    public void testToggleUserStatus() throws BusinessException {
        User created = userService.createUser(testUser, "Test@1234");
        boolean newStatus = userService.toggleUserStatus(created.getUserId(), ADMIN_USER_ID);
        assertFalse("Status should be toggled to inactive", newStatus);
        System.out.println("✓ testToggleUserStatus passed");
    }

    @Test(expected = BusinessException.class)
    public void testToggleSelfStatus() throws BusinessException {
        userService.toggleUserStatus(ADMIN_USER_ID, ADMIN_USER_ID);
    }

    // ===== SEARCH TESTS =====

    @Test
    public void testSearchUsers() throws BusinessException {
        userService.createUser(testUser, "Test@1234");
        List<User> results = userService.searchUsers("new");
        assertNotNull("Search results should not be null", results);
        assertTrue("Should find at least one user", results.size() > 0);
        System.out.println("✓ testSearchUsers passed");
    }

    @Test(expected = BusinessException.class)
    public void testSearchUsersEmptyTerm() throws BusinessException {
        userService.searchUsers("");
    }

    // ===== FIND TESTS =====

    @Test
    public void testFindUserById() throws BusinessException {
        User found = userService.findUserById(ADMIN_USER_ID);
        assertNotNull("User should be found", found);
        assertEquals("Username should be admin", "admin", found.getUsername());
        System.out.println("✓ testFindUserById passed");
    }

    @Test(expected = BusinessException.class)
    public void testFindUserByInvalidId() throws BusinessException {
        userService.findUserById(-1);
    }

    @Test
    public void testGetAllUsers() throws BusinessException {
        List<User> users = userService.getAllUsers();
        assertNotNull("User list should not be null", users);
        assertTrue("Should have at least one user", users.size() > 0);
        System.out.println("✓ testGetAllUsers passed");
    }
}
