package com.example.examify.dao;

import com.example.examify.model.Exam;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ExamDAO {
    private static final String DB_URL = "jdbc:sqlite:exams.db";

    public static int saveExam(Exam exam) {
        String sql = "INSERT INTO exams(user_id, start_time, end_time, score) VALUES (?, ?, ?, ?)";
        int generatedId = -1;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, exam.getStudentId());
            stmt.setString(2, exam.getStartTime().toString());
            stmt.setString(3, exam.getEndTime().toString());
            stmt.setDouble(4, exam.getScore());

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    generatedId = keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return generatedId;
    }

    public static List<Exam> getExamsByUserId(int userId) {
        List<Exam> exams = new ArrayList<>();
        String sql = "SELECT * FROM exams WHERE user_id = ? ORDER BY start_time DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Exam exam = new Exam(
                        userId,
                        LocalDateTime.parse(rs.getString("start_time")),
                        LocalDateTime.parse(rs.getString("end_time")),
                        rs.getDouble("score")
                );
                exam.setId(rs.getInt("id"));
                exams.add(exam);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return exams;
    }

}
