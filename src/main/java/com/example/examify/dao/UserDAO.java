package com.example.examify.dao;

import com.example.examify.model.User;
import com.example.examify.util.DBUtil;

import java.nio.DoubleBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO do operacji na użytkownikach (studenci i administratorzy).
 */
public class UserDAO {
    /**
     * Wyszukuje użytkownika po ID.
     *
     * @param id identyfikator użytkownika
     * @return obiekt użytkownika (jeśli istnieje)
     */
    public Optional<User> findById(int id) {
        String query = "SELECT id, username, email, password_hash, is_admin FROM users WHERE id = ?";
        try(Connection con = DBUtil.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id); // podstawiamy do pierwszego placeholdera id
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = mapRowtoUser(rs);
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * Wyszukuje użytkownika po loginie (nazwa lub e-mail).
     *
     * @param login nazwa użytkownika lub e-mail
     * @return użytkownik (jeśli znaleziony)
     */
    public Optional<User> findByLogin(String login) {
        String query = "SELECT id, username, email, password_hash, is_admin FROM users WHERE username = ? OR email = ?";
        try (Connection con = DBUtil.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, login);
            ps.setString(2, login);
            try(ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = mapRowtoUser(rs);
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * Zapisuje nowego użytkownika do bazy danych.
     *
     * @param user nowy użytkownik
     * @return true jeśli zapisano poprawnie
     */
    public boolean save(User user) {
        String query = "INSERT INTO users(username, email, password_hash, is_admin) VALUES(?, ?, ?, ?)";
        try (Connection con = DBUtil.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setBoolean(4, user.isAdmin());
            int affected = ps.executeUpdate();

            if (affected == 1) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        user.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Mapuje wynik zapytania SQL na obiekt użytkownika.
     *
     * @param rs wynik z bazy danych
     * @return obiekt User
     * @throws SQLException jeśli wystąpi błąd SQL
     */
    private User mapRowtoUser(ResultSet rs) throws SQLException {
        return new User(rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password_hash"),
                        rs.getBoolean("is_admin"));
    }

    /**
     * Pobiera wszystkich użytkowników (studentów i adminów).
     *
     * @return lista użytkowników
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setEmail(rs.getString("email"));
                u.setAdmin(rs.getBoolean("is_admin"));
                users.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Ustawia lub odbiera uprawnienia administratora dla użytkownika.
     *
     * @param userId ID użytkownika
     * @param isAdmin true jeśli ma być adminem
     */
    public void updateAdminStatus(int userId, boolean isAdmin) {
        String sql = "UPDATE users SET is_admin = ? WHERE id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, isAdmin);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Zwraca liczbę użytkowników niebędących administratorami.
     *
     * @return liczba studentów
     */
    public int countAllStudents() {
        String sql = "SELECT COUNT(*) FROM users WHERE is_admin = 0";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
