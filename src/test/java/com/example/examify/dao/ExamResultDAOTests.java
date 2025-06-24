package com.example.examify.dao;
import com.example.examify.model.Exam;
import com.example.examify.model.ExamResult;
import com.example.examify.model.ExamStats;
import com.example.examify.model.Question;
import com.example.examify.model.QuestionType;
import com.example.examify.model.User;
import com.example.examify.util.DBUtil;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ExamResultDAOTests {
    private static int userId = 1;
    private static int examId = 100;
    private static int resultId;

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

        // user create
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@mail.com");
        user.setPasswordHash("123");
        user.setAdmin(false);
        new UserDAO().save(user);
        userId = user.getId();

        // exam create
        Exam exam = new Exam();
        exam.setTitle("Math Exam");
        exam.setQuestionCount(2);
        exam.setTimeLimitMinutes(30);
        new ExamDAO().saveExam(exam);
        examId = exam.getId();

        // question create
        QuestionDAO qdao = new QuestionDAO();
        for (int i = 1; i <= 2; i++) {
            Question q = new Question();
            q.setExamId(examId);
            q.setText("Question " + i);
            q.setType(QuestionType.SINGLE_CHOICE);
            q.setOptions("A;B;C");
            q.setCorrectAnswer("A");
            qdao.saveQuestion(q);
        }

        // exam result create
        ExamResult result = new ExamResult(userId, examId,
                LocalDateTime.now().minusMinutes(5),
                LocalDateTime.now(),
                1.0
        );
        resultId = ExamResultDAO.saveExam(result);
        assertTrue(resultId > 0);
    }

    @Test
    @Order(1)
    void testGetExamsByUserId() {
        List<ExamResult> results = ExamResultDAO.getExamsByUserId(userId);
        assertFalse(results.isEmpty());
        assertEquals(examId, results.get(0).getExamId());
    }

    @Test
    @Order(2)
    void testGetResultsForExam() {
        List<ExamResult> results = new ExamResultDAO().getResultsForExam(examId);
        assertFalse(results.isEmpty());
        assertEquals(userId, results.get(0).getUserId());
    }

    @Test
    @Order(3)
    void testGetExamTitle() {
        String title = ExamResultDAO.getExamTitle(examId);
        assertEquals("Math Exam", title);
    }

    @Test
    @Order(4)
    void testGetStatsByExamId() {
        Optional<ExamStats> statsOpt = new ExamResultDAO().getStatsByExamId(examId);
        assertTrue(statsOpt.isPresent());

        ExamStats stats = statsOpt.get();
        assertEquals(1, stats.getTotalAttempts());
        assertEquals(1.0, stats.getAverageScore());
    }

    @Test
    @Order(5)
    void testGetAverageScore() {
        double avg = new ExamResultDAO().getAverageScore();
        assertTrue(avg > 0);
    }

    @Test
    @Order(6)
    void testCountAllAttempts() {
        int count = new ExamResultDAO().countAllAttempts();
        assertEquals(1, count);
    }

    @Test
    @Order(7)
    void testCountPassedExams() {
        int passed = new ExamResultDAO().countPassedExams();
        assertEquals(1, passed);
    }

    @Test
    @Order(8)
    void testAverageDuration() {
        double duration = new ExamResultDAO().getAverageDurationInMinutes();
        assertTrue(duration > 0 && duration < 10);
    }
}
