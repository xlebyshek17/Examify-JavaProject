package com.example.examify.service;
import com.example.examify.model.User;
import com.example.examify.util.DBUtil;
import org.junit.jupiter.api.*;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthServiceTests {
    private static AuthService authService;
    private static String username;
    private static String email;
    private static String password;

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

        authService = new AuthService();

        username = "user_" + UUID.randomUUID();
        email = "email_" + UUID.randomUUID() + "@mail.com";
        password = "securePassword123";
    }

    @Test
    @Order(1)
    void testRegisterNewUser() {
        var result = authService.register(username, email, password);
        assertEquals(RegistrationResult.OK, result);
    }

    @Test
    @Order(2)
    void testRegisterExistingUsername() {
        var result = authService.register(username, "new_" + email, "otherPass");
        assertEquals(RegistrationResult.USER_EXISTS, result);
    }

    @Test
    @Order(3)
    void testRegisterExistingEmail() {
        var result = authService.register("other_" + username, email, "otherPass");
        assertEquals(RegistrationResult.EMAIL_EXISTS, result);
    }

    @Test
    @Order(4)
    void testLoginWithCorrectCredentials() {
        Optional<User> userOpt = authService.login(username, password);
        assertTrue(userOpt.isPresent());
        assertEquals(username, userOpt.get().getUsername());
    }

    @Test
    @Order(5)
    void testLoginWithWrongPassword() {
        Optional<User> userOpt = authService.login(username, "wrongPassword");
        assertTrue(userOpt.isEmpty());
    }

    @Test
    @Order(6)
    void testLoginWithUnknownUser() {
        Optional<User> userOpt = authService.login("nonexistent", "irrelevant");
        assertTrue(userOpt.isEmpty());
    }
}
