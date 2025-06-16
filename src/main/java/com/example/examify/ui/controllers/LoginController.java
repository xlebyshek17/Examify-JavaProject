package com.example.examify.ui.controllers;

import com.example.examify.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import com.example.examify.service.AuthService;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class LoginController {
    public TextField loginField;
    public PasswordField passwordField;
    public Label errorField;
    AuthService authService = new AuthService();

    @FXML
    protected void onSignUpLinkClick(ActionEvent event) throws IOException {
        Parent newRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/examify/fxml/signup-view.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(newRoot));
        stage.show();
    }

    @FXML
    protected void onLogInButtonClick(ActionEvent event) throws IOException {
        String username = loginField.getText();
        String password = passwordField.getText();
        Optional<User> optUser = authService.login(username, password);
        if (optUser.isPresent()) {
            User user = optUser.get();

            String fxmlPath = user.isAdmin() ? "/com/example/examify/fxml/admin-view.fxml" : "/com/example/examify/fxml/student-view.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent newRoot = loader.load();

            if (!user.isAdmin()) {
                StudentController controller = loader.getController();
                controller.setUser(user);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(newRoot));
            stage.show();
            stage.centerOnScreen();
        }
        else {
            errorField.setText("Login or password is incorrect");
        }
    }

    @FXML
    protected void onBackButtonClick(ActionEvent event) throws IOException {
        Parent newRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/examify/fxml/welcome-view.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(newRoot));
        stage.setTitle("Examify — Welcome");
        stage.show();
    }
}
