package com.example.examify.dao;

import com.example.examify.model.ExamResult;
import com.example.examify.model.ExamStats;
import com.example.examify.util.DBUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO do obsługi wyników egzaminów i statystyk.
 */
public class ExamResultDAO {
    private static final String DB_URL = "jdbc:sqlite:exams.db";

    /**
     * Zapisuje wynik egzaminu do bazy.
     *
     * @param exam wynik egzaminu
     * @return ID wygenerowanego rekordu
     */
    public static int saveExam(ExamResult exam) {
        String sql = "INSERT INTO exam_results(user_id, exam_id, start_time, end_time, score) VALUES (?, ?, ?, ?, ?)";
        int generatedId = -1;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, exam.getUserId());
            stmt.setInt(2, exam.getExamId());
            stmt.setString(3, exam.getStartTime().toString());
            stmt.setString(4, exam.getEndTime().toString());
            stmt.setDouble(5, exam.getScore());

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    generatedId = keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return generatedId;
    }

    /**
     * Zwraca listę wyników dla danego użytkownika.
     *
     * @param userId ID użytkownika
     * @return lista wyników
     */
    public static List<ExamResult> getExamsByUserId(int userId) {
        List<ExamResult> exams = new ArrayList<>();
        String sql = "SELECT * FROM exam_results WHERE user_id = ? ORDER BY start_time DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ExamResult exam = new ExamResult(
                        userId,
                        rs.getInt("exam_id"),
                        LocalDateTime.parse(rs.getString("start_time")),
                        LocalDateTime.parse(rs.getString("end_time")),
                        rs.getDouble("score")
                );
                exam.setId(rs.getInt("id"));
                exams.add(exam);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return exams;
    }

    /**
     * Pobiera tytuł egzaminu po jego ID.
     *
     * @param examId identyfikator egzaminu
     * @return tytuł egzaminu
     */
    public static String getExamTitle(int examId) {
        String sql = "SELECT * FROM exams WHERE id = ?";
        String title = null;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, examId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                title = rs.getString("title");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return title;
    }

    /**
     * Oblicza statystyki dla konkretnego egzaminu.
     *
     * @param examId ID egzaminu
     * @return statystyki lub pusty obiekt
     */
    public Optional<ExamStats> getStatsByExamId(int examId) {
        String sql = """
        WITH question_count AS (
            SELECT exam_id, COUNT(*) AS total_questions
            FROM questions
            GROUP BY exam_id
        )
        SELECT
            AVG(er.score) AS average_score,
            MAX(er.score) AS max_score,
            MIN(er.score) AS min_score,
            COUNT(*) AS total_attempts,
            SUM(CASE
                    WHEN er.score > qc.total_questions / 2 THEN 1
                    ELSE 0
                END) * 100.0 / COUNT(*) AS pass_percentage,
            AVG(strftime('%s', er.end_time) - strftime('%s', er.start_time)) / 60.0 AS avg_duration_minutes
        FROM exam_results er
        JOIN question_count qc ON er.exam_id = qc.exam_id
        WHERE er.exam_id = ?;
        """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, examId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int totalAttempts = rs.getInt("total_attempts");
                double avg = rs.getDouble("average_score");
                double max = rs.getDouble("max_score");
                double min = rs.getDouble("min_score");
                double pass = rs.getDouble("pass_percentage");
                double avgTime = rs.getDouble("avg_duration_minutes");

                ExamStats stats = new ExamStats(totalAttempts, avg, max, min, pass, avgTime);
                return Optional.of(stats);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * Pobiera wszystkie wyniki danego egzaminu.
     *
     * @param examId ID egzaminu
     * @return lista wyników
     */
    public List<ExamResult> getResultsForExam(int examId) {
        List<ExamResult> results = new ArrayList<>();
        String sql = "SELECT * FROM exam_results WHERE exam_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, examId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                int userId = rs.getInt("user_id");
                LocalDateTime start = LocalDateTime.parse(rs.getString("start_time"));
                LocalDateTime end = LocalDateTime.parse(rs.getString("end_time"));
                double score = rs.getDouble("score");

                results.add(new ExamResult(id, userId, examId, start, end, score));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    /**
     * Oblicza średni wynik ze wszystkich egzaminów.
     *
     * @return średnia punktacja
     */
    public double getAverageScore() {
        String sql = "SELECT AVG(score) FROM exam_results";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getDouble(1) : 0.0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    /**
     * Liczy, ile egzaminów zakończyło się wynikiem powyżej progu zdawalności.
     *
     * @return liczba zdanych egzaminów
     */
    public int countPassedExams() {
        String sql = """
        SELECT COUNT(*) FROM exam_results er
        JOIN exams e ON er.exam_id = e.id
        WHERE er.score >= (
            SELECT COUNT(*) / 2.0
            FROM questions q
            WHERE q.exam_id = er.exam_id
        )
    """;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Zwraca całkowitą liczbę podejść do egzaminów.
     *
     * @return liczba podejść
     */
    public int countAllAttempts() {
        String sql = "SELECT COUNT(*) FROM exam_results";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Oblicza średni czas trwania egzaminów w minutach.
     *
     * @return średnia długość egzaminu
     */
    public double getAverageDurationInMinutes() {
        String sql = """
        SELECT AVG((julianday(end_time) - julianday(start_time)) * 24 * 60)
        FROM exam_results
        WHERE start_time IS NOT NULL AND end_time IS NOT NULL
    """;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getDouble(1) : 0.0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        }
    }


}
