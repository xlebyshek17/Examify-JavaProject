package com.example.examify.ui.controllers;

import com.example.examify.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * Kontroler widoku z wynikiem egzaminu.
 * Wyświetla wynik i umożliwia powrót do menu użytkownika.
 */
public class ResultController {

    @FXML
    private Label scoreLabel;

    private User user;

    /**
     * Ustawia użytkownika, dla którego wyświetlany jest wynik.
     *
     * @param user obiekt użytkownika
     */
    public void setUser(User user) { this.user = user; }

    /**
     * Ustawia tekst z wynikiem egzaminu.
     *
     * @param score liczba zdobytych punktów
     * @param total całkowita liczba punktów możliwych do zdobycia
     */
    public void setScore(int score, int total) {
        scoreLabel.setText("Uzyskany wynik: " + score + " / " + total);
    }

    /**
     * Obsługuje powrót do menu głównego studenta.
     * Przełącza scenę na widok studenta.
     */
    @FXML
    private void handleReturnToMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/examify/fxml/student-view.fxml"));
            Parent root = loader.load();

            StudentController controller = loader.getController();
            controller.setUser(user);

            Stage stage = (Stage) scoreLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
