package com.example.examify.dao;

import com.example.examify.model.Question;
import com.example.examify.model.QuestionType;
import com.example.examify.util.DBUtil;

import java.sql.*;
import java.util.*;

/**
 * DAO do zarządzania pytaniami egzaminacyjnymi.
 */
public class QuestionDAO {
    private static final String DB_URL = "jdbc:sqlite:exams.db";

    /**
     * Pobiera wszystkie pytania przypisane do egzaminu.
     *
     * @param examId ID egzaminu
     * @return lista pytań
     */
    public static List<Question> getQuestionsForExam(int examId) {
        List<Question> list = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE exam_id = ?";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, examId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Question(
                        rs.getInt("id"),
                        rs.getInt("exam_id"),
                        rs.getString("text"),
                        QuestionType.fromLabel(rs.getString("type")),
                        rs.getString("options"),
                        rs.getString("correct_answer")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }

        return list;
    }

    /**
     * Zapisuje pytanie do bazy danych.
     * Aktualizuje, jeśli pytanie już istnieje.
     *
     * @param question pytanie do zapisania
     * @return ID pytania
     * @throws SQLException jeśli wystąpi błąd SQL
     */
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

    /**
     * Sprawdza, czy pytanie o podanym ID istnieje.
     *
     * @param id ID pytania
     * @return true jeśli istnieje
     * @throws SQLException jeśli wystąpi błąd SQL
     */
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

    /**
     * Wstawia nowe pytanie do bazy danych.
     *
     * @param question pytanie do dodania
     * @return wygenerowane ID
     * @throws SQLException jeśli wystąpi błąd SQL
     */
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

    /**
     * Pobiera pytanie na podstawie jego ID.
     *
     * @param id identyfikator pytania
     * @return pytanie (jeśli istnieje)
     * @throws SQLException jeśli wystąpi błąd SQL
     */
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

    /**
     * Zwraca wszystkie pytania w systemie.
     *
     * @return lista wszystkich pytań
     * @throws SQLException jeśli wystąpi błąd SQL
     */
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

    /**
     * Aktualizuje istniejące pytanie.
     *
     * @param question pytanie do aktualizacji
     * @return ID pytania
     * @throws SQLException jeśli wystąpi błąd SQL
     */
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

    /**
     * Mapuje wynik z bazy danych na obiekt Question.
     *
     * @param rs wynik zapytania SQL
     * @return obiekt pytania
     * @throws SQLException jeśli wystąpi błąd SQL
     */
    private Question mapRowToQuestion(ResultSet rs) throws SQLException {
        return new Question(rs.getInt("id"),
                rs.getInt("exam_id"),
                rs.getString("text"),
                QuestionType.fromLabel(rs.getString("type")),
                rs.getString("options"),
                rs.getString("correct_answer"));
    }

    /**
     * Usuwa pytanie o podanym ID.
     *
     * @param id ID pytania
     * @return true jeśli usunięto
     * @throws SQLException jeśli wystąpi błąd SQL
     */
    public boolean deleteQuestionById(int id) throws SQLException {
        String sql = "DELETE FROM questions WHERE id = ?";
        try (Connection con = DBUtil.getConnection();
        PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Pobiera wszystkie pytania przypisane do danego egzaminu.
     *
     * @param examId identyfikator egzaminu
     * @return lista pytań
     * @throws SQLException jeśli wystąpi błąd SQL
     */
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

    /**
     * Zlicza liczbę pytań przypisanych do egzaminu.
     *
     * @param examId identyfikator egzaminu
     * @return liczba pytań
     * @throws SQLException jeśli wystąpi błąd SQL
     */
    public int getQuestionCountForExam(int examId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM questions WHERE exam_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, examId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

}

