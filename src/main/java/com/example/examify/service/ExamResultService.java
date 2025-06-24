package com.example.examify.service;

import com.example.examify.dao.ExamResultDAO;
import com.example.examify.model.ExamResult;
import com.example.examify.model.ExamStats;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ExamResultService {
    private final ExamResultDAO examResultDAO = new ExamResultDAO();

    public Optional<ExamStats> getExamStats(int examId) throws SQLException {
        return examResultDAO.getStatsByExamId(examId);
    }

    public List<ExamResult> getResultsForExam(int examId) {
        return examResultDAO.getResultsForExam(examId);
    }

    public double getAverageScore() {
        return examResultDAO.getAverageScore();
    }

    public int countPassedExams() {
        return examResultDAO.countPassedExams();
    }

    public int countAllAttemts() {
        return examResultDAO.countAllAttempts();
    }

    public double getAverageDurationInMinutes() {
        return examResultDAO.getAverageDurationInMinutes();
    }
}
