package com.example.examify.service;

import com.example.examify.dao.UserDAO;
import com.example.examify.model.User;
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

        // brakuje funkcji haszującej
        // String hash =

        User newUser = new User(username, email, plainPassword);
        return userDAO.save(newUser) ? RegistrationResult.OK : RegistrationResult.ERROR;
    }

    public Optional<User> login(String login, String plainPassword) {
        Optional<User> optUser = userDAO.findByLogin(login);
        if (optUser.isPresent()) {
            User user = optUser.get();
            if (plainPassword.equals(user.getPasswordHash())) {
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }
}
