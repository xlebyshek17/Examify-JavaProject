package com.example.examify.ui.controllers;

import com.example.examify.dao.AnswerDAO;
import com.example.examify.dao.ExamDAO;
import com.example.examify.dao.QuestionDAO;
import com.example.examify.model.Answer;
import com.example.examify.model.Exam;
import com.example.examify.model.Question;
import com.example.examify.model.User;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExamController {

    // ===== pola wstrzykiwane z FXML =====
    @FXML private Label questionLabel;
    @FXML private RadioButton optionA, optionB, optionC, optionD;
    @FXML private ToggleGroup optionsGroup;
    @FXML private Label progressLabel;

    private LocalDateTime startTime;
    private List<Answer> givenAnswers = new ArrayList<>();

    // ===== timer =====
    private int timeRemaining = 30; // sekundy
    private Timeline timer;
    @FXML private Label timerLabel;

    private void startTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeRemaining--;
            timerLabel.setText("Pozostały czas: " + timeRemaining + " s");

            if (timeRemaining <= 0) {
                timer.stop();
                autoFinishExam();
            }
        }));
        timer.setCycleCount(timeRemaining);
        timer.play();
    }

    private void autoFinishExam() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Koniec czasu");
        alert.setHeaderText("Czas na egzamin minął!");
        alert.setContentText("Twój wynik zostanie zapisany.");
        alert.setOnHidden(e -> finishExam());
        alert.show();
    }



    // ===== logika =====
    private User user;
    private List<Question> questions;
    private int index = 0;
    private int score = 0;        // liczba poprawnych

    // ===== API wywoływane z StudentController =====
    public void setUser(User user) { this.user = user; }
    public void startExam() {
        startTime = LocalDateTime.now();
        questions = QuestionDAO.getRandomQuestions(5);
        showQuestion();
        startTimer();
    }

    // ===== obsługa FXML =====
    @FXML
    private void handleNext() {
        if (optionsGroup.getSelectedToggle() == null) return;      // nic nie zaznaczono

        RadioButton selected = (RadioButton) optionsGroup.getSelectedToggle();
        String chosen = selected.getText();
        Question q = questions.get(index);

        if (chosen.equals(q.getCorrectAnswer())) score++;

        Answer ans = new Answer(0, 0, q.getId(), chosen, chosen.equals(q.getCorrectAnswer()));
        givenAnswers.add(ans);


        index++;
        if (index < questions.size()) {
            showQuestion();
        } else {
            finishExam();
        }
    }

    // ===== pomocnicze =====
    private void showQuestion() {
        Question q = questions.get(index);
        List<String> opts = new ArrayList<>(q.getOptionList());
        Collections.shuffle(opts); // losowa kolejność odpowiedzi


        questionLabel.setText((index + 1) + ". " + q.getText());
        optionA.setText(opts.get(0));
        optionB.setText(opts.get(1));
        optionC.setText(opts.get(2));
        optionD.setText(opts.get(3));
        optionsGroup.selectToggle(null);                     // wyczyść wybór

        progressLabel.setText("Pytanie " + (index + 1) + " z " + questions.size());
    }

    private void finishExam() {
        try {
            if (timer != null) timer.stop();
            LocalDateTime endTime = LocalDateTime.now();

            Exam exam = new Exam(user.getId(), startTime, endTime, score);
            int examId = ExamDAO.saveExam(exam);

            for (Answer a : givenAnswers) {
                a.setExamId(examId);
                AnswerDAO.saveAnswer(a);
            }

            // Przejdź do widoku wyników
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/examify/fxml/result-view.fxml"));
            Parent root = loader.load();

            ResultController controller = loader.getController();
            controller.setScore(score, questions.size());
            controller.setUser(user);

            Stage stage = (Stage) questionLabel.getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
