package com.example.examify.service;

import com.example.examify.model.Exam;
import com.example.examify.util.DBUtil;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ExamServiceTests {
    private static ExamService examService;
    private static int examId;

    @BeforeAll
    static void setup() {
        System.setProperty("db.mode", "test");
        DBUtil.init();

        try (var conn = DBUtil.getConnection();
             var stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM exams");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        examService = new ExamService();
    }

    @Test
    @Order(1)
    void testSaveExam() throws SQLException {
        Exam exam = new Exam();
        exam.setTitle("Databases 101");
        exam.setQuestionCount(12);
        exam.setTimeLimitMinutes(30);

        examId = examService.saveExam(exam);
        assertTrue(examId > 0);
    }

    @Test
    @Order(2)
    void testGetExamById() throws SQLException {
        Optional<Exam> examOpt = examService.getExamById(examId);
        assertTrue(examOpt.isPresent());
        assertEquals("Databases 101", examOpt.get().getTitle());
    }

    @Test
    @Order(3)
    void testFindByTitle() throws SQLException {
        Optional<Exam> examOpt = examService.findByTitle("Databases 101");
        assertTrue(examOpt.isPresent());
        assertEquals(examId, examOpt.get().getId());
    }

    @Test
    @Order(4)
    void testUpdateExam() throws SQLException {
        Optional<Exam> examOpt = examService.getExamById(examId);
        assertTrue(examOpt.isPresent());

        Exam exam = examOpt.get();
        exam.setTitle("Advanced SQL");
        exam.setQuestionCount(20);
        exam.setTimeLimitMinutes(40);

        int updatedId = examService.updateExam(exam);
        assertEquals(examId, updatedId);

        Optional<Exam> updated = examService.getExamById(updatedId);
        assertTrue(updated.isPresent());
        assertEquals("Advanced SQL", updated.get().getTitle());
        assertEquals(20, updated.get().getQuestionCount());
    }

    @Test
    @Order(5)
    void testGetAllExams() throws SQLException {
        List<Exam> exams = examService.getAllExams();
        assertFalse(exams.isEmpty());
        assertTrue(exams.stream().anyMatch(e -> e.getId() == examId));
    }

    @Test
    @Order(6)
    void testDeleteExam() throws SQLException {
        boolean deleted = examService.deleteExam(examId);
        assertTrue(deleted);

        Optional<Exam> examOpt = examService.getExamById(examId);
        assertTrue(examOpt.isEmpty());
    }
}

