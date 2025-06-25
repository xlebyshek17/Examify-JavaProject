package com.example.examify.service;

import com.example.examify.dao.UserDAO;
import com.example.examify.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

/**
 * Serwis odpowiadający za rejestrację i logowanie użytkowników.
 */
public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    /**
     * Rejestruje nowego użytkownika w systemie.
     *
     * @param username nazwa użytkownika
     * @param email adres e-mail
     * @param plainPassword hasło w formie jawnej
     * @return rezultat rejestracji
     */
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

    /**
     * Próbuje zalogować użytkownika na podstawie loginu i hasła.
     *
     * @param login nazwa użytkownika lub e-mail
     * @param inputPassword hasło podane przez użytkownika
     * @return zalogowany użytkownik (jeśli dane są poprawne)
     */
    public Optional<User> login(String login, String inputPassword) {
        Optional<User> optUser = userDAO.findByLogin(login);
        if (optUser.isPresent()) {
            User user = optUser.get();
            if (user.isAdmin()) {
                if (inputPassword.equals(user.getPasswordHash())) {
                    return Optional.of(user);
                }
            }
            try {
                if (BCrypt.checkpw(inputPassword, user.getPasswordHash())) {
                    return Optional.of(user);
                }
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid password hash for user: " + user.getUsername());
            }
        }

        return Optional.empty();
    }
}
