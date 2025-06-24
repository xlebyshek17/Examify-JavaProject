package com.example.examify.dao;

import com.example.examify.model.Exam;
import com.example.examify.util.DBUtil;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ExamDAOTests {
    private static ExamDAO examDAO;
    private static int examId;

    @BeforeAll
    static void setup() {
        System.setProperty("db.mode", "test"); // in-memory db
        DBUtil.init();

        try (var conn = DBUtil.getConnection();
             var stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM users");
            stmt.execute("DELETE FROM exams");
            stmt.execute("DELETE FROM questions");
            stmt.execute("DELETE FROM exam_results");
            stmt.execute("DELETE FROM answers");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        examDAO = new ExamDAO();
    }

    @Test
    @Order(1)
    void testSaveExam() throws SQLException {
        Exam exam = new Exam();
        exam.setTitle("Java Basics");
        exam.setQuestionCount(10);
        exam.setTimeLimitMinutes(30);

        examId = examDAO.saveExam(exam);
        assertTrue(examId > 0);
    }

    @Test
    @Order(2)
    void testFindById() throws SQLException {
        Optional<Exam> found = examDAO.findById(examId);
        assertTrue(found.isPresent());
        assertEquals("Java Basics", found.get().getTitle());
    }

    @Test
    @Order(3)
    void testFindByTitle() {
        Optional<Exam> found = examDAO.findByTitle("Java Basics");
        assertTrue(found.isPresent());
        assertEquals(examId, found.get().getId());
    }

    @Test
    @Order(4)
    void testUpdateExam() throws SQLException {
        Optional<Exam> found = examDAO.findById(examId);
        assertTrue(found.isPresent());

        Exam exam = found.get();
        exam.setTitle("Java Advanced");
        exam.setQuestionCount(15);
        exam.setTimeLimitMinutes(45);

        int updatedId = examDAO.updateExam(exam);
        assertEquals(examId, updatedId);

        Optional<Exam> updated = examDAO.findById(updatedId);
        assertTrue(updated.isPresent());
        assertEquals("Java Advanced", updated.get().getTitle());
        assertEquals(15, updated.get().getQuestionCount());
    }

    @Test
    @Order(5)
    void testGetAllExams() {
        List<Exam> exams = examDAO.getAllExams();
        assertFalse(exams.isEmpty());
        assertTrue(exams.stream().anyMatch(e -> e.getId() == examId));
    }

    @Test
    @Order(6)
    void testDeleteExamById() throws SQLException {
        boolean deleted = examDAO.deleteById(examId);
        assertTrue(deleted);

        Optional<Exam> after = examDAO.findById(examId);
        assertTrue(after.isEmpty());
    }
}
