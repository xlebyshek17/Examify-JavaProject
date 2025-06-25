package com.example.examify.ui.controllers;

import com.example.examify.service.ExamResultService;
import com.example.examify.service.ExamService;
import com.example.examify.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Kontroler panelu administratora. Odpowiada za nawigację i wyświetlanie statystyk systemu.
 */
public class AdminController {
    @FXML private BorderPane adminMainPane;
    @FXML private Label totalExamLabel;
    @FXML private Label studentsTotalLabel;
    @FXML private Label averageScoreLabel;
    @FXML private Label examPassedLabel;
    @FXML private Label attempsTotalLabel;
    @FXML private Label averageDurationLabel;
    @FXML private Label adminLabel;

    @FXML
    private StackPane contentPane;

    private final ExamService examService = new ExamService();
    private final UserService userService = new UserService();
    private final ExamResultService examResultService = new ExamResultService();

    /**
     * Inicjalizuje widok oraz wczytuje dane statystyczne z usług.
     *
     * @throws SQLException w przypadku problemu z bazą danych
     */
    @FXML
    public void initialize() throws SQLException {
        int totalExams = examService.getAllExams().size();
        int totalStudents = userService.getStudentsCount();
        double averageScore = examResultService.getAverageScore();
        int passedExams = examResultService.countPassedExams();
        int countAllAttempts = examResultService.countAllAttemts();
        double averageDuration = examResultService.getAverageDurationInMinutes();
        setAdminDashboardStats(totalExams, totalStudents, averageScore, passedExams, countAllAttempts, averageDuration);
    }

    /**
     * Ustawia dane statystyczne na ekranie administratora.
     *
     * @param totalExams liczba egzaminów
     * @param totalStudents liczba zarejestrowanych studentów
     * @param averageScore średni wynik egzaminu
     * @param passedExams liczba zdanych egzaminów
     * @param totalAttempts całkowita liczba podejść
     * @param avgDuration średni czas trwania egzaminu
     */
    public void setAdminDashboardStats(int totalExams, int totalStudents, double averageScore,
                                       int passedExams, int totalAttempts, double avgDuration) {
        totalExamLabel.setText(String.valueOf(totalExams));
        studentsTotalLabel.setText(String.valueOf(totalStudents));
        averageScoreLabel.setText(String.format("%.2f", averageScore));
        examPassedLabel.setText(String.valueOf(passedExams));
        attempsTotalLabel.setText(String.valueOf(totalAttempts));
        averageDurationLabel.setText(String.format("%.1f min", avgDuration));
    }

    /**
     * Wylogowuje administratora i przenosi do ekranu powitalnego.
     *
     * @param actionEvent zdarzenie kliknięcia przycisku
     * @throws IOException w przypadku błędu ładowania widoku
     */
    @FXML
    private void onButtonLogOutClick(ActionEvent actionEvent) throws IOException {
        Parent newRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/examify/fxml/welcome-view.fxml")));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(newRoot));
        stage.show();
        stage.centerOnScreen();
    }

    /**
     * Ładuje widok zarządzania egzaminami.
     *
     * @param actionEvent zdarzenie kliknięcia przycisku
     */
    @FXML
    public void showExams(ActionEvent actionEvent) {
        adminLabel.setText("Admin - Exams Management");
        loadView("/com/example/examify/fxml/admin_exams-view.fxml");
    }

    /**
     * Ładuje widok zarządzania wynikami egzaminów.
     *
     * @param actionEvent zdarzenie kliknięcia przycisku
     */
    @FXML
    public void showResults(ActionEvent actionEvent) {
        adminLabel.setText("Admin - Results Management");
        loadView("/com/example/examify/fxml/admin_results-view.fxml");
    }

    /**
     * Ładuje widok zarządzania użytkownikami.
     *
     * @param actionEvent zdarzenie kliknięcia przycisku
     */
    @FXML
    public void showUsers(ActionEvent actionEvent) {
        adminLabel.setText("Admin - Users Management");
        loadView("/com/example/examify/fxml/admin_users-view.fxml");
    }

    /**
     * Wczytuje i osadza nowy widok FXML w głównym panelu.
     *
     * @param fxmlPath ścieżka do pliku FXML
     */
    private void loadView(String fxmlPath) {
        try {
            Node view = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlPath)));
            contentPane.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Obsługuje kliknięcie w element menu (np. logo), przeładowując widok główny administratora.
     *
     * @param mouseEvent zdarzenie kliknięcia
     */
    @FXML
    public void handleMenuClick(MouseEvent mouseEvent) {
        try {
            Parent newRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/examify/fxml/admin-view.fxml")));
            Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(newRoot));
            stage.show();
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}