package com.example.examify.dao;

import com.example.examify.model.Question;
import com.example.examify.model.QuestionType;

import java.sql.*;
import java.util.*;

public class QuestionDAO {
    private static final String DB_URL = "jdbc:sqlite:exams.db";

    /* Daje wszystkie pytania dla danego egzaminu */
    public static List<Question> getQuestionsForExam(int examId) {
        List<Question> list = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE exam_id = ?";

        try (Connection c = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, examId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Question(
                        rs.getInt("id"),
                        rs.getInt("exam_id"),
                        rs.getString("text"),
                        QuestionType.valueOf(rs.getString("type").toUpperCase()),
                        rs.getString("options"),
                        rs.getString("correct_answer")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }

        return list;
    }

}
