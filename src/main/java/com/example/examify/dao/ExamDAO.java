package com.example.examify.dao;

import com.example.examify.model.Exam;
import com.example.examify.model.User;
import com.example.examify.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExamDAO {
    public List<Exam> getAllExams() {
        List<Exam> exams = new ArrayList<>();
        String query = "SELECT * FROM exams";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Exam e = new Exam();
                e.setId(rs.getInt("id"));
                e.setTitle(rs.getString("title"));
                e.setQuestionCount(rs.getInt("question_count"));
                e.setCreatedAt(rs.getTimestamp("created_at"));
                e.setTimeLimitMinutes(rs.getInt("time_limit_minutes"));
                exams.add(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exams;
    }

    public int saveExam(Exam exam) throws SQLException {
        String query = "INSERT INTO exams(title, question_count, time_limit_minutes) VALUES (?, ?, ?)";
        int generatedId = -1;
        try (Connection con = DBUtil.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, exam.getTitle());
            ps.setInt(3, exam.getQuestionCount());
            ps.setInt(4, exam.getTime_limit_minutes());
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 1) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1);
                        exam.setId(generatedId);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return generatedId;
    }

    public int updateExam(Exam exam) throws SQLException {
        String query = "UPDATE exams SET title = ?, question_count = ?, time_limit_minutes = ? WHERE id = ?";
        int updatedId = -1;
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, exam.getTitle());
            ps.setInt(2, exam.getQuestionCount());
            ps.setInt(3, exam.getTime_limit_minutes());
            int affected = ps.executeUpdate();
            if (affected == 1) {
                updatedId =  exam.getId();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updatedId;
    }

    public Optional<Exam> findById(int id) throws SQLException {
        String query = "SELECT * FROM exams WHERE id = ?";
        try (Connection con = DBUtil.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Exam exam = mapRowToExam(rs);
                    return Optional.of(exam);
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private Exam mapRowToExam(ResultSet rs) throws SQLException {
        return new Exam(rs.getInt("id"),
                rs.getString("title"),
                rs.getInt("question_count"),
                rs.getInt("time_limit_minutes"),
                rs.getTimestamp("created_at")
        );
    }
}
