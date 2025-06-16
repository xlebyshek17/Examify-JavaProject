package com.example.examify.service;

import com.example.examify.dao.ExamDAO;
import com.example.examify.model.Exam;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ExamService {
    private final ExamDAO examDao = new ExamDAO();

    public int saveExam(Exam exam) throws SQLException {
        return examDao.saveExam(exam);
    }

    public List<Exam> getAllExams() throws SQLException{
        return examDao.getAllExams();
    }

    public int updateExam(Exam exam) throws SQLException {
        return examDao.updateExam(exam);
    }

    public Optional<Exam> getExamById(int id) throws SQLException {
        return examDao.findById(id);
    }
}
