package com.example.examify.service;

import com.example.examify.dao.ExamDAO;
import com.example.examify.model.Exam;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Serwis do zarządzania egzaminami.
 */
public class ExamService {
    private final ExamDAO examDao = new ExamDAO();

    /**
     * Zapisuje nowy egzamin.
     *
     * @param exam obiekt egzaminu
     * @return ID egzaminu
     * @throws SQLException jeśli wystąpi błąd bazy danych
     */
    public int saveExam(Exam exam) throws SQLException {
        return examDao.saveExam(exam);
    }

    /**
     * Zwraca listę wszystkich egzaminów.
     *
     * @return lista egzaminów
     * @throws SQLException jeśli wystąpi błąd bazy danych
     */
    public List<Exam> getAllExams() throws SQLException{
        return examDao.getAllExams();
    }

    /**
     * Aktualizuje istniejący egzamin.
     *
     * @param exam egzamin do aktualizacji
     * @return ID zaktualizowanego egzaminu
     * @throws SQLException jeśli wystąpi błąd bazy danych
     */
    public int updateExam(Exam exam) throws SQLException {
        return examDao.updateExam(exam);
    }

    /**
     * Wyszukuje egzamin po ID.
     *
     * @param id identyfikator egzaminu
     * @return egzamin (jeśli istnieje)
     * @throws SQLException jeśli wystąpi błąd bazy danych
     */
    public Optional<Exam> getExamById(int id) throws SQLException {
        return examDao.findById(id);
    }

    /**
     * Usuwa egzamin po ID.
     *
     * @param id identyfikator egzaminu
     * @return true jeśli usunięto
     * @throws SQLException jeśli wystąpi błąd bazy danych
     */
    public boolean deleteExam(int id) throws SQLException {
        return examDao.deleteById(id);
    }

    /**
     * Wyszukuje egzamin po tytule.
     *
     * @param title tytuł egzaminu
     * @return egzamin (jeśli znaleziony)
     * @throws SQLException jeśli wystąpi błąd bazy danych
     */
    public Optional<Exam> findByTitle(String title) throws SQLException {
        return examDao.findByTitle(title);
    }
}
