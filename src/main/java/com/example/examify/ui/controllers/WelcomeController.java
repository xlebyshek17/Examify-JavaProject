package com.example.examify.ui.controllers;

import com.example.examify.MainApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Kontroler widoku powitalnego aplikacji.
 * Obsługuje przejścia do widoku logowania oraz rejestracji użytkownika.
 */
public class WelcomeController {
    /**
     * Obsługuje kliknięcie przycisku "Sign In" i przechodzi do ekranu logowania.
     *
     * @param event zdarzenie akcji (kliknięcie przycisku)
     * @throws IOException gdy nie uda się załadować widoku
     */
    @FXML
    protected void onSignInButtonClick(ActionEvent event) throws IOException {
        Parent newRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/examify/fxml/login-view.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(newRoot));
        stage.setTitle("Examify");
        stage.show();
    }

    /**
     * Obsługuje kliknięcie przycisku "Sign Up" i przechodzi do ekranu rejestracji.
     *
     * @param event zdarzenie akcji (kliknięcie przycisku)
     * @throws IOException gdy nie uda się załadować widoku
     */
    @FXML
    protected void onSignUpButtonClick(ActionEvent event) throws IOException {
        Parent newRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/examify/fxml/signup-view.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(newRoot));
        stage.setTitle("Examify");
        stage.show();
    }
}
