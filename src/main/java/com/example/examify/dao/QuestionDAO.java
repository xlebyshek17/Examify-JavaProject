package com.example.examify.dao;

import com.example.examify.model.Question;
import com.example.examify.model.QuestionType;
import com.example.examify.util.DBUtil;

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
                        QuestionType.valueOf(rs.getString("type").toUpperCase()),
                        rs.getString("options"),
                        rs.getString("correct_answer")
                ));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int saveQuestion(Question question) throws SQLException {
        System.out.println("Question from dao: " + question);
        if (question.getId() > 0 && existsById(question.getId())) {
            System.out.println("Question already exists: " + question);
            return updateQuestion(question);
        } else {
            System.out.println("Question not exists: " + question);
            return insertQuestion(question);
        }
    }

    private boolean existsById(int id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM questions WHERE id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private int insertQuestion(Question question) throws SQLException {
        String query = "INSERT INTO questions (exam_id, text, type, options, correct_answer) VALUES(?, ?, ?, ?, ?)";
        int generatedId = -1;
        try (Connection con = DBUtil.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, question.getExamId());
            ps.setString(2, question.getText());
            ps.setString(3, question.getType().getLabel());
            ps.setString(4, question.getOptions());
            ps.setString(5, question.getCorrectAnswer());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 1) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1);
                        question.setId(generatedId);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return generatedId;
    }

    public Optional<Question> getQuestionById(int id) throws SQLException {
        String sql = "SELECT * FROM questions WHERE id = ?";

        try (Connection con = DBUtil.getConnection();
        PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Question question = mapRowToQuestion(rs);
                    return Optional.of(question);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }


    public List<Question> getAllQuestions() throws SQLException {
        List<Question> questions = new ArrayList<>();
        String query = "SELECT * FROM questions";
        try (Connection con = DBUtil.getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Question question = mapRowToQuestion(rs);
                questions.add(question);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }

    public int updateQuestion(Question question) throws SQLException {
        String query = "UPDATE questions SET exam_id = ?, text = ?, type = ?, options = ?, correct_answer = ? WHERE id = ?";
        int updatedId = -1;
        try (Connection con = DBUtil.getConnection();
        PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, question.getExamId());
            ps.setString(2, question.getText());
            ps.setString(3, question.getType().getLabel());
            ps.setString(4, question.getOptions());
            ps.setString(5, question.getCorrectAnswer());
            ps.setInt(6, question.getId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 1) {
                updatedId = question.getId();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return updatedId;
    }

    private Question mapRowToQuestion(ResultSet rs) throws SQLException {
        return new Question(rs.getInt("id"),
                rs.getInt("exam_id"),
                rs.getString("text"),
                QuestionType.fromLabel(rs.getString("type")),
                rs.getString("options"),
                rs.getString("correct_answer"));
    }

    public boolean deleteQuestionById(int id) throws SQLException {
        String sql = "DELETE FROM questions WHERE id = ?";
        try (Connection con = DBUtil.getConnection();
        PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    public List<Question> getQuestionsByExamId(int examId) throws SQLException {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE exam_id = ?";
        try (Connection con = DBUtil.getConnection();
        PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Question question = mapRowToQuestion(rs);
                    questions.add(question);
                }
            }
        }
        return questions;
    }
}

