package com.example.examify.dao;

import com.example.examify.model.Answer;
import com.example.examify.model.Question;
import com.example.examify.model.QuestionType;
import com.example.examify.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnswerDAO {
    private static final String DB_URL = "jdbc:sqlite:exams.db";

    public static void saveAnswer(Answer answer) {
        String sql = "INSERT INTO answers(exam_result_id, question_id, answer, is_correct) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, answer.getExamResultId());
            stmt.setInt(2, answer.getQuestionId());
            stmt.setString(3, answer.getAnswer());
            stmt.setBoolean(4, answer.isCorrect());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Answer> getAnswersByExamId(int examId) {
        List<Answer> answers = new ArrayList<>();
        String sql = """
            SELECT a.*, q.text, q.correct_answer, q.type, q.options
            FROM answers a
            JOIN questions q ON a.question_id = q.id
            WHERE a.exam_result_id = ?
        """;


        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, examId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Answer a = new Answer(
                        rs.getInt("id"),
                        examId,
                        rs.getInt("question_id"),
                        rs.getString("answer"),
                        rs.getBoolean("is_correct")
                );

                Question q = new Question(
                        rs.getInt("question_id"),
                        rs.getInt("exam_result_id"),
                        rs.getString("text"),
                        QuestionType.fromLabel(rs.getString("type")),
                        rs.getString("options"),
                        rs.getString("correct_answer")
                );


                a.setQuestion(q);
                answers.add(a);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return answers;
    }

}
