package com.example.examify.service;

import com.example.examify.dao.UserDAO;
import com.example.examify.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Serwis do zarządzania użytkownikami.
 */
public class UserService {
    private final UserDAO userDAO = new UserDAO();

    /**
     * Zwraca listę wszystkich użytkowników.
     *
     * @return lista użytkowników
     */
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    /**
     * Zmienia status administratora dla danego użytkownika.
     *
     * @param userId ID użytkownika
     * @param isAdmin true, jeśli ma być adminem
     */
    public void updateAdminStatus(int userId, boolean isAdmin) {
        userDAO.updateAdminStatus(userId, isAdmin);
    }

    /**
     * Pobiera użytkownika po ID.
     *
     * @param userId identyfikator użytkownika
     * @return użytkownik (jeśli istnieje)
     */
    public Optional<User> getUserById(int userId) {
        return userDAO.findById(userId);
    }

    /**
     * Zlicza wszystkich użytkowników nie będących administratorami.
     *
     * @return liczba studentów
     */
    public int getStudentsCount() {
        return userDAO.countAllStudents();
    }
}
