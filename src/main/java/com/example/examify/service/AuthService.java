package com.example.examify.service;

import com.example.examify.dao.UserDAO;
import com.example.examify.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    public RegistrationResult register(String username, String email, String plainPassword) {
        if (userDAO.findByLogin(username).isPresent()) {
            return RegistrationResult.USER_EXISTS;
        }
        else if (userDAO.findByLogin(email).isPresent()) {
            return RegistrationResult.EMAIL_EXISTS;
        }

        String salt = BCrypt.gensalt(12);
        String hashPassword = BCrypt.hashpw(plainPassword, salt);

        User newUser = new User(username, email, hashPassword);
        return userDAO.save(newUser) ? RegistrationResult.OK : RegistrationResult.ERROR;
    }

    public Optional<User> login(String login, String inputPassword) {
        Optional<User> optUser = userDAO.findByLogin(login);
        if (optUser.isPresent()) {
            User user = optUser.get();
            if (user.isAdmin()) {
                if (inputPassword.equals(user.getPasswordHash())) {
                    return Optional.of(user);
                }
            }
            if (BCrypt.checkpw(inputPassword, user.getPasswordHash())) {
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }
}
