package com.example.examify.service;

import com.example.examify.dao.QuestionDAO;
import com.example.examify.model.Exam;
import com.example.examify.model.Question;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class QuestionService {
    private final QuestionDAO questionDAO = new QuestionDAO();

    public int updateOrInsertQuestion(Question question) throws SQLException {
        return questionDAO.saveQuestion(question);
    }

    public List<Question> getAllQuestions() throws SQLException{
        return questionDAO.getAllQuestions();
    }

    public Optional<Question> getQuestionById(int id) throws SQLException {
        return questionDAO.getQuestionById(id);
    }

    public int updateQuestion(Question question) throws SQLException {
        return questionDAO.updateQuestion(question);
    }

    public boolean deleteQuestion(int id) throws SQLException {
        return questionDAO.deleteQuestionById(id);
    }

    public List<Question> getQuestionsByExamId(int examId) throws SQLException {
        return questionDAO.getQuestionsByExamId(examId);
    }
}
