package com.example.examify.ui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.example.examify.model.User;
import javafx.scene.control.Label;

import java.io.IOException;
import java.util.Objects;

public class StudentController {

    @FXML
    private Label welcomeLabel;

    private User user;

    public void setUser(User user) {
        this.user = user;
        welcomeLabel.setText("Witaj, " + user.getUsername() + "!");
    }

    @FXML
    private void onButtonLogOutClick(ActionEvent actionEvent) throws IOException {
        Parent newRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/examify/fxml/welcome-view.fxml")));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(newRoot));
        stage.show();
    }

    @FXML
    private void startExam(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/examify/fxml/exam-view.fxml"));
            Parent root = loader.load();

            // Przypisanie użytkownika
            ExamController controller = loader.getController();
            controller.setUser(user);     // ← przekazujemy
            controller.startExam();       // ← uruchamiamy

            // Wyświetlenie nowej sceny
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

            // Dopiero po załadowaniu sceny uruchom logikę egzaminu
            controller.startExam();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
