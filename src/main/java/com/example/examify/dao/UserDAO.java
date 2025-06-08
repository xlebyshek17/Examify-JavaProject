package com.example.examify.dao;

import com.example.examify.model.User;
import com.example.examify.util.DBUtil;

import java.nio.DoubleBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserDAO {
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

    private User mapRowtoUser(ResultSet rs) throws SQLException {
        return new User(rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password_hash"),
                        rs.getBoolean("is_admin"));
    }
}
