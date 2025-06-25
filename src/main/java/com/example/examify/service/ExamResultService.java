package com.example.examify.service;

import com.example.examify.dao.ExamResultDAO;
import com.example.examify.model.ExamResult;
import com.example.examify.model.ExamStats;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Serwis do zarządzania wynikami egzaminów i statystykami.
 */
public class ExamResultService {
    private final ExamResultDAO examResultDAO = new ExamResultDAO();

    /**
     * Zwraca statystyki egzaminu.
     *
     * @param examId ID egzaminu
     * @return statystyki egzaminu
     * @throws SQLException jeśli wystąpi błąd bazy danych
     */
    public Optional<ExamStats> getExamStats(int examId) throws SQLException {
        return examResultDAO.getStatsByExamId(examId);
    }

    /**
     * Pobiera wyniki dla danego egzaminu.
     *
     * @param examId ID egzaminu
     * @return lista wyników
     */
    public List<ExamResult> getResultsForExam(int examId) {
        return examResultDAO.getResultsForExam(examId);
    }

    /**
     * Zwraca średni wynik ze wszystkich egzaminów.
     *
     * @return średni wynik
     */
    public double getAverageScore() {
        return examResultDAO.getAverageScore();
    }

    /**
     * Liczy, ile egzaminów zostało zaliczonych.
     *
     * @return liczba zaliczonych egzaminów
     */
    public int countPassedExams() {
        return examResultDAO.countPassedExams();
    }

    /**
     * Zwraca liczbę wszystkich podejść do egzaminów.
     *
     * @return liczba prób
     */
    public int countAllAttemts() {
        return examResultDAO.countAllAttempts();
    }

    /**
     * Oblicza średni czas trwania egzaminu.
     *
     * @return średni czas w minutach
     */
    public double getAverageDurationInMinutes() {
        return examResultDAO.getAverageDurationInMinutes();
    }
}
