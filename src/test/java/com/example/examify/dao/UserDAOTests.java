package com.example.examify.dao;

import com.example.examify.util.DBUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.*;

import com.example.examify.model.User;
import org.junit.jupiter.api.*;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserDAOTests {
    private static UserDAO userDAO;
    private static User testUser;

    @BeforeAll
    static void setup() {
        System.setProperty("db.mode", "test");

        DBUtil.init();


        try (var conn = DBUtil.getConnection();
             var stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM users");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        userDAO = new UserDAO();
    }

    @Test
    @Order(1)
    void testSaveUser() {
        testUser = new User();
        testUser.setUsername("testuser_" + UUID.randomUUID());
        testUser.setEmail("test_" + UUID.randomUUID() + "@mail.com");
        testUser.setPasswordHash("hashed_password");
        testUser.setAdmin(false);

        boolean result = userDAO.save(testUser);
        assertTrue(result);
        assertTrue(testUser.getId() > 0);
    }

    @Test
    @Order(2)
    void testFindById() {
        Optional<User> found = userDAO.findById(testUser.getId());
        assertTrue(found.isPresent());
        assertEquals(testUser.getUsername(), found.get().getUsername());
    }

    @Test
    @Order(3)
    void testFindByLogin_username() {
        Optional<User> found = userDAO.findByLogin(testUser.getUsername());
        assertTrue(found.isPresent());
        assertEquals(testUser.getEmail(), found.get().getEmail());
    }

    @Test
    @Order(4)
    void testFindByLogin_email() {
        Optional<User> found = userDAO.findByLogin(testUser.getEmail());
        assertTrue(found.isPresent());
        assertEquals(testUser.getUsername(), found.get().getUsername());
    }

    @Test
    @Order(5)
    void testUpdateAdminStatus() {
        userDAO.updateAdminStatus(testUser.getId(), true);
        Optional<User> updated = userDAO.findById(testUser.getId());
        assertTrue(updated.isPresent());
        assertTrue(updated.get().isAdmin());
    }

    @Test
    @Order(6)
    void testGetAllUsers() {
        assertFalse(userDAO.getAllUsers().isEmpty());
    }

    @Test
    @Order(7)
    void testCountAllStudents() {
        int count = userDAO.countAllStudents();
        assertTrue(count >= 0);
    }
}
