package com.example.examify.ui.controllers;

import com.example.examify.dao.AnswerDAO;
import com.example.examify.model.Answer;
import com.example.examify.model.Question;
import com.example.examify.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.util.List;

public class AnswerReviewController {

    @FXML
    private ListView<String> answerListView;

    private int examId;
    private User user;

    public void setData(int examId, User user) {
        this.examId = examId;
        this.user = user;
        loadAnswers();
    }

    private void loadAnswers() {
        List<Answer> answers = AnswerDAO.getAnswersByExamId(examId);

        for (Answer a : answers) {
            Question q = a.getQuestion(); // musi być wcześniej ustawiony w DAO
            String status = a.isCorrect() ? "✅" : "❌";

            String item = String.format("%s\nTwoja odpowiedź: %s  %s\nPoprawna odpowiedź: %s",
                    q.getText(),
                    a.getAnswer(),
                    status,
                    q.getCorrectAnswer());

            answerListView.getItems().add(item);
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/examify/fxml/history-view.fxml"));
            Parent root = loader.load();

            HistoryController controller = loader.getController();
            controller.setUser(user);

            Stage stage = (Stage) answerListView.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
