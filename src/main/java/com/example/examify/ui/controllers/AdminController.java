package com.example.examify.ui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class AdminController {
    public Label adminLabel;
    @FXML
    private StackPane contentPane;

    @FXML
    private void onButtonLogOutClick(ActionEvent actionEvent) throws IOException {
        Parent newRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/examify/fxml/welcome-view.fxml")));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(newRoot));
        stage.show();
    }

    @FXML
    public void showExams(ActionEvent actionEvent) {
        adminLabel.setText("Admin - Exams Management");
        loadView("/com/example/examify/fxml/admin_exams-view.fxml");
    }

    @FXML
    public void showResults(ActionEvent actionEvent) {
        adminLabel.setText("Admin - Results Management");
        loadView("/com/example/examify/fxml/admin_results-view.fxml");
    }

    @FXML
    public void showUsers(ActionEvent actionEvent) {
        adminLabel.setText("Admin - Users Management");
        loadView("/com/example/examify/fxml/admin_users-view.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            Node view = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlPath)));
            contentPane.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
