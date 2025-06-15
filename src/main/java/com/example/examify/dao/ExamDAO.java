package com.example.examify.dao;

import com.example.examify.model.Exam;
import com.example.examify.model.User;
import com.example.examify.util.DBUtil;

import java.sql.*;
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
}
