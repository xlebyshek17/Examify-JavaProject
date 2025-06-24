package com.example.examify.service;

import com.example.examify.dao.UserDAO;
import com.example.examify.model.User;

import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserDAO userDAO = new UserDAO();

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public void updateAdminStatus(int userId, boolean isAdmin) {
        userDAO.updateAdminStatus(userId, isAdmin);
    }

    public Optional<User> getUserById(int userId) {
        return userDAO.findById(userId);
    }

    public int getStudentsCount() {
        return userDAO.countAllStudents();
    }
}
