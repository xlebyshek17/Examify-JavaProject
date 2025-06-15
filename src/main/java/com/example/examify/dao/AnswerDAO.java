package com.example.examify.dao;

import com.example.examify.model.Answer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AnswerDAO {
    private static final String DB_URL = "jdbc:sqlite:exams.db";

    public static void saveAnswer(Answer answer) {
        String sql = "INSERT INTO answers(exam_id, question_id, answer, is_correct) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, answer.getExamId());
            stmt.setInt(2, answer.getQuestionId());
            stmt.setString(3, answer.getAnswer());
            stmt.setBoolean(4, answer.isCorrect());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
