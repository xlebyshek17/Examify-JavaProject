package com.example.examify.dao;

import com.example.examify.model.Question;

import java.sql.*;
import java.util.*;

public class QuestionDAO {
    private static final String DB_URL = "jdbc:sqlite:exams.db";

    /** Zwraca count losowych pytań z tabeli questions */
    public static List<Question> getRandomQuestions(int count) {
        List<Question> list = new ArrayList<>();
        String sql = "SELECT * FROM questions ORDER BY RANDOM() LIMIT ?";

        try (Connection c = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, count);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Question(
                        rs.getInt("id"),
                        rs.getString("text"),
                        rs.getString("type"),
                        rs.getString("options")      // „A;B;C;D”
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
