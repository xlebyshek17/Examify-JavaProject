package com.example.examify.service;

import com.example.examify.dao.ExamDAO;
import com.example.examify.dao.ExamResultDAO;
import com.example.examify.dao.QuestionDAO;
import com.example.examify.dao.UserDAO;
import com.example.examify.model.*;
import com.example.examify.util.DBUtil;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ExamResultServiceTests {
    private static ExamResultService examResultService;
    private static int examId;
    private static int userId;

    @BeforeAll
    static void setup() throws SQLException {
        System.setProperty("db.mode", "test");
        DBUtil.init();

        try (var conn = DBUtil.getConnection();
             var stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM users");
            stmt.execute("DELETE FROM exams");
            stmt.execute("DELETE FROM questions");
            stmt.execute("DELETE FROM exam_results");
            stmt.execute("DELETE FROM answers");
        }

        examResultService = new ExamResultService();

        // user
        User user = new User("tester", "tester@mail.com", "hash123");
        new UserDAO().save(user);
        userId = user.getId();

        // exam
        Exam exam = new Exam("Sample Exam", 2, 30);
        new ExamDAO().saveExam(exam);
        examId = exam.getId();

        // questions
        QuestionDAO qdao = new QuestionDAO();
        for (int i = 0; i < 2; i++) {
            Question q = new Question();
            q.setExamId(examId);
            q.setText("Question " + (i + 1));
            q.setType(QuestionType.SINGLE_CHOICE);
            q.setOptions("A;B;C");
            q.setCorrectAnswer("A");
            qdao.saveQuestion(q);
        }

        // exam result
        ExamResult result = new ExamResult(userId, examId,
                LocalDateTime.now().minusMinutes(10),
                LocalDateTime.now(),
                1.0);
        ExamResultDAO.saveExam(result);
    }

    @Test
    @Order(1)
    void testGetResultsForExam() {
        List<ExamResult> results = examResultService.getResultsForExam(examId);
        assertFalse(results.isEmpty());
        assertEquals(examId, results.get(0).getExamId());
    }

    @Test
    @Order(2)
    void testGetExamStats() throws Exception {
        Optional<ExamStats> statsOpt = examResultService.getExamStats(examId);
        assertTrue(statsOpt.isPresent());
        ExamStats stats = statsOpt.get();
        assertEquals(1, stats.getTotalAttempts());
        assertEquals(1.0, stats.getAverageScore());
    }

    @Test
    @Order(3)
    void testGetAverageScore() {
        double avg = examResultService.getAverageScore();
        assertTrue(avg > 0.0);
    }

    @Test
    @Order(4)
    void testCountPassedExams() {
        int count = examResultService.countPassedExams();
        assertEquals(1, count); // score = 1.0, exam has 2 questions => passed
    }

    @Test
    @Order(5)
    void testCountAllAttempts() {
        int count = examResultService.countAllAttemts();
        assertEquals(1, count);
    }

    @Test
    @Order(6)
    void testGetAverageDurationInMinutes() {
        double minutes = examResultService.getAverageDurationInMinutes();
        assertTrue(minutes > 0.0 && minutes <= 15.0);
    }
}
