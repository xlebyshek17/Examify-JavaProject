package com.example.examify.dao;

import com.example.examify.model.Exam;
import com.example.examify.model.User;
import com.example.examify.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExamDAO {
    public static List<Exam> getAllExams() {
        List<Exam> exams = new ArrayList<>();
        String query = "SELECT * FROM exams";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Exam e = mapRowToExam(rs);
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
            PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, exam.getTitle());
            ps.setInt(2, exam.getQuestionCount());
            ps.setInt(3, exam.getTime_limit_minutes());
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
            ps.setInt(4, exam.getId());
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

    public boolean deleteById(int id) throws SQLException {
        String delQ = "DELETE FROM questions WHERE exam_id = ?";
        String delE = "DELETE FROM exams WHERE id = ?";

        try (Connection con = DBUtil.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement stm1 = con.prepareStatement(delQ);
                PreparedStatement stm2 = con.prepareStatement(delE);) {
                stm1.setInt(1, id);
                stm1.executeUpdate();

                stm2.setInt(1, id);
                int affected = stm2.executeUpdate();

                con.commit();
                return affected > 0;
            } catch (SQLException e) {
                con.rollback();
                e.printStackTrace();
            } finally {
                con.setAutoCommit(true);
            }
        }
        return false;
    }

    public Optional<Exam> findByTitle(String title) {
        String sql = "SELECT * FROM exams WHERE title = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, title);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Exam exam = mapRowToExam(rs);
                return Optional.of(exam);
            }

        } catch (SQLException e) {
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
