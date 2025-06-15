package com.example.examify.ui.controllers;

import com.example.examify.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ExamController {

    private User user;

    @FXML
    private Label examTitleLabel = new Label("Egzamin dla: anonim");

    public void setUser(User user) {
        this.user = user;
    }

    public void startExam() {
        examTitleLabel.setText("Egzamin dla: " + user.getUsername());
        // TODO: losowanie pytań, start timera itd.
    }
}