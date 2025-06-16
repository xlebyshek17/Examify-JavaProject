package com.example.examify.ui.controllers;

import com.example.examify.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class ResultController {

    @FXML
    private Label scoreLabel;

    private User user;

    public void setUser(User user) { this.user = user; }

    public void setScore(int score, int total) {
        scoreLabel.setText("Uzyskany wynik: " + score + " / " + total);
    }

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
