package com.example.examify.service;

import com.example.examify.dao.QuestionDAO;
import com.example.examify.model.Exam;
import com.example.examify.model.Question;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Serwis do zarządzania pytaniami egzaminacyjnymi.
 */
public class QuestionService {
    private final QuestionDAO questionDAO = new QuestionDAO();

    /**
     * Wstawia lub aktualizuje pytanie.
     *
     * @param question pytanie do zapisania
     * @return ID pytania
     * @throws SQLException jeśli wystąpi błąd bazy danych
     */
    public int updateOrInsertQuestion(Question question) throws SQLException {
        return questionDAO.saveQuestion(question);
    }

    /**
     * Pobiera wszystkie pytania.
     *
     * @return lista pytań
     * @throws SQLException jeśli wystąpi błąd bazy danych
     */
    public List<Question> getAllQuestions() throws SQLException{
        return questionDAO.getAllQuestions();
    }

    /**
     * Pobiera pytanie po ID.
     *
     * @param id identyfikator pytania
     * @return pytanie (jeśli istnieje)
     * @throws SQLException jeśli wystąpi błąd bazy danych
     */
    public Optional<Question> getQuestionById(int id) throws SQLException {
        return questionDAO.getQuestionById(id);
    }

    /**
     * Aktualizuje pytanie.
     *
     * @param question pytanie do aktualizacji
     * @return ID pytania
     * @throws SQLException jeśli wystąpi błąd bazy danych
     */
    public int updateQuestion(Question question) throws SQLException {
        return questionDAO.updateQuestion(question);
    }

    /**
     * Usuwa pytanie po ID.
     *
     * @param id identyfikator pytania
     * @return true jeśli usunięto
     * @throws SQLException jeśli wystąpi błąd bazy danych
     */
    public boolean deleteQuestion(int id) throws SQLException {
        return questionDAO.deleteQuestionById(id);
    }

    /**
     * Pobiera pytania przypisane do egzaminu.
     *
     * @param examId ID egzaminu
     * @return lista pytań
     * @throws SQLException jeśli wystąpi błąd bazy danych
     */
    public List<Question> getQuestionsByExamId(int examId) throws SQLException {
        return questionDAO.getQuestionsByExamId(examId);
    }

    /**
     * Zwraca liczbę pytań w egzaminie.
     *
     * @param examId ID egzaminu
     * @return liczba pytań
     * @throws SQLException jeśli wystąpi błąd bazy danych
     */
    public int getQuestionCountForExam(int examId) throws SQLException {
        return questionDAO.getQuestionCountForExam(examId);
    }
}
