package com.example.examify.dao;

import com.example.examify.model.*;
import com.example.examify.util.DBUtil;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AnswerDAOTests {
    private static int userId;
    private static int examId;
    private static int questionId;
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

        // create user
        User user = new User("student", "student@example.com", "hashed_pass");
        new UserDAO().save(user);
        userId = user.getId();

        // create exam
        Exam exam = new Exam("Answer Test", 1, 15);
        new ExamDAO().saveExam(exam);
        examId = exam.getId();

        // create question
        Question question = new Question();
        question.setExamId(examId);
        question.setText("What's 2+2?");
        question.setType(QuestionType.SINGLE_CHOICE);
        question.setOptions("1;2;3;4");
        question.setCorrectAnswer("4");
        new QuestionDAO().saveQuestion(question);
        questionId = question.getId();

        // create exam result
        ExamResult result = new ExamResult(userId, examId,
                LocalDateTime.now().minusMinutes(2),
                LocalDateTime.now(),
                1.0
        );
        resultId = ExamResultDAO.saveExam(result);
    }

    @Test
    @Order(1)
    void testSaveAnswer() {
        Answer answer = new Answer(0, resultId, questionId, "4", true);
        AnswerDAO.saveAnswer(answer);
    }

    @Test
    @Order(2)
    void testGetAnswersByExamId() {
        List<Answer> answers = AnswerDAO.getAnswersByExamId(resultId);
        assertFalse(answers.isEmpty());

        Answer a = answers.get(0);
        assertEquals(resultId, a.getExamResultId());
        assertEquals(questionId, a.getQuestionId());
        assertEquals("4", a.getAnswer());
        assertTrue(a.isCorrect());

        Question q = a.getQuestion();
        assertNotNull(q);
        assertEquals("What's 2+2?", q.getText());
        assertEquals("4", q.getCorrectAnswer());
    }
}
