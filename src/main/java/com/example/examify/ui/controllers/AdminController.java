package com.example.examify.ui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class AdminController {
    @FXML
    private void onButtonLogOutClick(ActionEvent actionEvent) throws IOException {
        Parent newRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/examify/fxml/welcome-view.fxml")));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(newRoot));
        stage.show();
    }
}
