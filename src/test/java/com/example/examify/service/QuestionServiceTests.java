package com.example.examify.service;

import com.example.examify.model.Question;
import com.example.examify.model.QuestionType;
import com.example.examify.util.DBUtil;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class QuestionServiceTests {
    private static QuestionService questionService;
    private static int questionId;
    private static final int examId = 777;

    @BeforeAll
    static void setup() {
        System.setProperty("db.mode", "test");
        DBUtil.init();

        try (var conn = DBUtil.getConnection();
             var stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM questions");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        questionService = new QuestionService();
    }

    @Test
    @Order(1)
    void testInsertQuestion() throws SQLException {
        Question q = new Question();
        q.setExamId(examId);
        q.setText("What is 5 + 5?");
        q.setType(QuestionType.SINGLE_CHOICE);
        q.setOptions("5;10;15;20");
        q.setCorrectAnswer("10");

        questionId = questionService.updateOrInsertQuestion(q);
        assertTrue(questionId > 0);
    }

    @Test
    @Order(2)
    void testGetQuestionById() throws SQLException {
        Optional<Question> opt = questionService.getQuestionById(questionId);
        assertTrue(opt.isPresent());
        assertEquals("What is 5 + 5?", opt.get().getText());
    }

    @Test
    @Order(3)
    void testUpdateQuestion() throws SQLException {
        Optional<Question> opt = questionService.getQuestionById(questionId);
        assertTrue(opt.isPresent());

        Question q = opt.get();
        q.setText("What is 6 + 6?");
        q.setCorrectAnswer("12");

        int updatedId = questionService.updateQuestion(q);
        assertEquals(questionId, updatedId);

        Optional<Question> updated = questionService.getQuestionById(updatedId);
        assertTrue(updated.isPresent());
        assertEquals("12", updated.get().getCorrectAnswer());
    }

    @Test
    @Order(4)
    void testGetAllQuestions() throws SQLException {
        List<Question> all = questionService.getAllQuestions();
        assertFalse(all.isEmpty());
        assertTrue(all.stream().anyMatch(q -> q.getId() == questionId));
    }

    @Test
    @Order(5)
    void testGetQuestionsByExamId() throws SQLException {
        List<Question> questions = questionService.getQuestionsByExamId(examId);
        assertEquals(1, questions.size());
        assertEquals(questionId, questions.get(0).getId());
    }

    @Test
    @Order(6)
    void testGetQuestionCountForExam() throws SQLException {
        int count = questionService.getQuestionCountForExam(examId);
        assertEquals(1, count);
    }

    @Test
    @Order(7)
    void testDeleteQuestion() throws SQLException {
        boolean deleted = questionService.deleteQuestion(questionId);
        assertTrue(deleted);

        Optional<Question> after = questionService.getQuestionById(questionId);
        assertTrue(after.isEmpty());
    }

}
