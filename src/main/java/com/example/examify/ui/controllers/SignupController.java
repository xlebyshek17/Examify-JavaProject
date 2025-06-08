package com.example.examify.ui.controllers;

import com.example.examify.service.RegistrationResult;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.example.examify.service.AuthService;

import java.io.IOException;
import java.util.Objects;

public class SignupController {
    @FXML private Button signUpButton;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorField;

    private final AuthService authService = new AuthService();

    public void initialize() {
        errorField.setVisible(false);
        signUpButton.setDisable(true);

        ChangeListener<String> cl = (observableValue, oldValue, newValue) -> validateAll();

        usernameField.textProperty().addListener(cl);
        emailField.textProperty().addListener(cl);
        passwordField.textProperty().addListener(cl);
    }

    private void validateAll() {
        String u = usernameField.getText();
        String e = emailField.getText();
        String p = passwordField.getText();

        // 1) Username
        if (!u.isEmpty()) {
            if (u.length() < 5 || u.length() > 20) {
                showError("Username must be 5-20 characters");
                return;
            }
        } else {
            hideError();
            signUpButton.setDisable(true);
            return;
        }

        // 2) Email
        if (!e.isEmpty()) {
            if (!e.matches("\\S+@\\S+\\.\\S+")) {
                showError("Incorrect email format");
                return;
            }
        } else {
            hideError();
            signUpButton.setDisable(true);
            return;
        }

        // 3) Password
        if (!p.isEmpty()) {
            if (p.length() < 6 || p.length() > 20) {
                showError("Password must be 6-20 characters");
                return;
            }
        } else {
            hideError();
            signUpButton.setDisable(true);
            return;
        }

        hideError();
        signUpButton.setDisable(false);
    }

    private void showError(String msg) {
        errorField.setText(msg);
        errorField.setVisible(true);
        signUpButton.setDisable(true);
    }

    private void hideError() {
        errorField.setText("");
        errorField.setVisible(false);
    }

    @FXML
    protected void onButtonBackClick(ActionEvent event) throws IOException {
        Parent newRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/examify/fxml/welcome-view.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(newRoot));
        stage.setTitle("Examify — Welcome");
        stage.show();
    }

    @FXML
    public void onButtonSignUpClick(ActionEvent event)  throws IOException {
        errorField.setVisible(true);
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();


        RegistrationResult res = authService.register(username, email, password);
        switch (res) {
            case OK:
                Parent newRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/examify/fxml/login-view.fxml")));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(newRoot));
                stage.show();
                break;
            case USER_EXISTS:
                errorField.setText("Username is already taken");
                break;
            case EMAIL_EXISTS:
                errorField.setText("Email is already taken");
                break;
            case ERROR:
                errorField.setText("Cannot register user, please try again");
                break;
        }
    }
}
