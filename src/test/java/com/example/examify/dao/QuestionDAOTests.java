package com.example.examify.dao;
import com.example.examify.model.Question;
import com.example.examify.model.QuestionType;
import com.example.examify.util.DBUtil;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class QuestionDAOTests {
    private static QuestionDAO questionDAO;
    private static int insertedId;
    private static final int examId = 101;

    @BeforeAll
    static void setup() {
        System.setProperty("db.mode", "test"); // in-memory DB
        DBUtil.init();

        try (var conn = DBUtil.getConnection();
             var stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM questions");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        questionDAO = new QuestionDAO();
    }

    @Test
    @Order(1)
    void testInsertQuestion() throws SQLException {
        Question q = new Question();
        q.setExamId(examId);
        q.setText("What is 2 + 2?");
        q.setType(QuestionType.SINGLE_CHOICE);
        q.setOptions("1;2;3;4");
        q.setCorrectAnswer("4");

        int id = questionDAO.saveQuestion(q);
        insertedId = id;

        assertTrue(id > 0);
    }

    @Test
    @Order(2)
    void testGetQuestionById() throws SQLException {
        Optional<Question> q = questionDAO.getQuestionById(insertedId);
        assertTrue(q.isPresent());
        assertEquals("What is 2 + 2?", q.get().getText());
    }

    @Test
    @Order(3)
    void testUpdateQuestion() throws SQLException {
        Optional<Question> q = questionDAO.getQuestionById(insertedId);
        assertTrue(q.isPresent());

        Question question = q.get();
        question.setText("What is 3 + 3?");
        question.setCorrectAnswer("6");

        int updatedId = questionDAO.saveQuestion(question);
        assertEquals(insertedId, updatedId);

        Optional<Question> updated = questionDAO.getQuestionById(updatedId);
        assertTrue(updated.isPresent());
        assertEquals("6", updated.get().getCorrectAnswer());
    }

    @Test
    @Order(4)
    void testGetAllQuestions() throws SQLException {
        List<Question> questions = questionDAO.getAllQuestions();
        assertFalse(questions.isEmpty());
        assertTrue(questions.stream().anyMatch(q -> q.getId() == insertedId));
    }

    @Test
    @Order(5)
    void testGetQuestionsByExamId() throws SQLException {
        List<Question> examQuestions = questionDAO.getQuestionsByExamId(examId);
        assertFalse(examQuestions.isEmpty());
        assertEquals(examId, examQuestions.get(0).getExamId());
    }

    @Test
    @Order(6)
    void testGetQuestionCountForExam() throws SQLException {
        int count = questionDAO.getQuestionCountForExam(examId);
        assertTrue(count >= 1);
    }

    @Test
    @Order(7)
    void testDeleteQuestionById() throws SQLException {
        boolean deleted = questionDAO.deleteQuestionById(insertedId);
        assertTrue(deleted);

        Optional<Question> after = questionDAO.getQuestionById(insertedId);
        assertTrue(after.isEmpty());
    }
}
