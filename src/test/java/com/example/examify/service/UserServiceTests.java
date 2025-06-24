package com.example.examify.service;

import com.example.examify.dao.UserDAO;
import com.example.examify.model.User;
import com.example.examify.util.DBUtil;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTests {
    private static UserService userService;
    private static User testUser;
    private UserDAO userDAO;

    @BeforeAll
    static void setup() throws SQLException {
        System.setProperty("db.mode", "test");

        DBUtil.init();

        try (var conn = DBUtil.getConnection();
             var stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM users");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        UserDAO userDAO = new UserDAO();
        userService = new UserService();

        testUser = new User();
        testUser.setUsername("testuser_" + UUID.randomUUID());
        testUser.setEmail("test_" + UUID.randomUUID() + "@mail.com");
        testUser.setPasswordHash("pass123");
        testUser.setAdmin(false);
        userDAO.save(testUser);
    }

    @Test
    @Order(1)
    void testGetUserById() {
        Optional<User> result = userService.getUserById(testUser.getId());
        assertTrue(result.isPresent());
        assertEquals(testUser.getUsername(), result.get().getUsername());
    }

    @Test
    @Order(2)
    void testGetAllUsers() {
        List<User> all = userService.getAllUsers();
        assertFalse(all.isEmpty());
        assertTrue(all.stream().anyMatch(u -> u.getId() == testUser.getId()));
    }

    @Test
    @Order(3)
    void testUpdateAdminStatus() {
        userService.updateAdminStatus(testUser.getId(), true);
        Optional<User> updated = userService.getUserById(testUser.getId());
        assertTrue(updated.isPresent());
        assertTrue(updated.get().isAdmin());
    }

    @Test
    @Order(4)
    void testGetStudentsCount() {
        int count = userService.getStudentsCount();
        assertEquals(0, count);
    }
}
