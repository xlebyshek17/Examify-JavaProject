package com.example.examify.ui.controllers;

import com.example.examify.dao.AnswerDAO;
import com.example.examify.dao.ExamResultDAO;
import com.example.examify.dao.QuestionDAO;
import com.example.examify.model.*;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Kontroler obsługujący przebieg egzaminu — wyświetlanie pytań, zbieranie odpowiedzi, odliczanie czasu,
 * zapis wyniku i przejście do widoku wyników.
 */
public class ExamController {
    @FXML private Label questionLabel;
    @FXML private ToggleGroup optionsGroup;
    @FXML private Label progressLabel;

    @FXML private VBox singleChoiceBox, shortAnswerBox, trueFalseBox;
    @FXML private TextField shortAnswerField;
    @FXML private ToggleGroup tfGroup;

    private LocalDateTime startTime;
    private List<Answer> givenAnswers = new ArrayList<>();

    private int timeRemaining; // sekundy
    private Timeline timer;
    @FXML private Label timerLabel;

    private User user;
    private List<Question> questions;
    private int index = 0;
    private int score = 0;

    private Exam exam;

    /**
     * Uruchamia timer odliczający czas egzaminu i aktualizujący etykietę z pozostałym czasem.
     * Po wyczerpaniu czasu wywołuje automatyczne zakończenie egzaminu.
     */
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

    /**
     * Wywoływane automatycznie po wyczerpaniu czasu egzaminu — pokazuje alert informujący i kończy egzamin.
     */
    private void autoFinishExam() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Koniec czasu");
        alert.setHeaderText("Czas na egzamin minął!");
        alert.setContentText("Twój wynik zostanie zapisany.");
        alert.setOnHidden(e -> finishExam());
        alert.show();
    }

    /**
     * Ustawia użytkownika oraz egzamin do przeprowadzenia.
     *
     * @param user  aktualny użytkownik
     * @param exam  egzamin do przeprowadzenia
     */
    public void setUserAndExam(User user, Exam exam) {
        this.user = user;
        this.exam = exam;
    }

    /**
     * Rozpoczyna egzamin: ustawia czas, pobiera pytania, przygotowuje interfejs i startuje timer.
     */
    public void startExam() {
        startTime = LocalDateTime.now();

        timeRemaining = exam.getTime_limit_minutes() * 60;

        questions = QuestionDAO.getQuestionsForExam(exam.getId());

        if (questions.size() > exam.getQuestionCount()) {
            Collections.shuffle(questions);
            questions = questions.subList(0, exam.getQuestionCount());
        }

        showQuestion();
        startTimer();

    }

    /**
     * Obsługuje zdarzenie kliknięcia przycisku "Dalej" — pobiera wybraną odpowiedź, zapisuje ją,
     * sprawdza poprawność, wyświetla kolejne pytanie lub kończy egzamin.
     */
    @FXML
    private void handleNext() {
        Question q = questions.get(index);
        String chosen = null;

        switch (q.getType()) {
            case SINGLE_CHOICE -> {
                RadioButton selected = (RadioButton) optionsGroup.getSelectedToggle();
                if (selected == null) return;
                chosen = selected.getText();
            }

            case SHORT_ANSWER -> {
                chosen = shortAnswerField.getText().trim();
                if (chosen.isEmpty()) return;
            }

            case TRUE_FALSE -> {
                RadioButton selected = (RadioButton) tfGroup.getSelectedToggle();
                if (selected == null) return;
                if (Objects.equals(selected.getText(), "Prawda")) {
                    chosen = "True";
                } else {
                    chosen = "False";
                }
            }
        }

        boolean isCorrect = chosen.equalsIgnoreCase(q.getCorrectAnswer());
        Answer ans = new Answer(0, 0, q.getId(), chosen, isCorrect);
        givenAnswers.add(ans);

        if (isCorrect) {score++;}

        index++;
        if (index < questions.size()) {
            showQuestion();
        } else {
            finishExam();
        }
    }

    /**
     * Wyświetla bieżące pytanie i odpowiedni typ odpowiedzi (jednokrotny wybór, prawda/fałsz, krótka odpowiedź).
     */
    private void showQuestion() {
        Question q = questions.get(index);
        progressLabel.setText("Pytanie " + (index + 1) + " z " + questions.size());
        questionLabel.setText(q.getText());

        singleChoiceBox.setVisible(false); singleChoiceBox.setManaged(false);
        shortAnswerBox.setVisible(false); shortAnswerBox.setManaged(false);
        trueFalseBox.setVisible(false);   trueFalseBox.setManaged(false);

        switch (q.getType()) {
            case SINGLE_CHOICE -> {
                singleChoiceBox.getChildren().clear();
                optionsGroup.getToggles().clear();

                List<String> opts = new ArrayList<>(q.getOptionList());
                Collections.shuffle(opts);

                for (String opt : opts) {
                    RadioButton rb = new RadioButton(opt);
                    rb.setWrapText(true);
                    rb.setToggleGroup(optionsGroup);
                    singleChoiceBox.getChildren().add(rb);
                }


                singleChoiceBox.setVisible(true); singleChoiceBox.setManaged(true);
            }

            case SHORT_ANSWER -> {
                shortAnswerField.clear();
                shortAnswerBox.setVisible(true); shortAnswerBox.setManaged(true);
            }

            case TRUE_FALSE -> {
                tfGroup.selectToggle(null);
                trueFalseBox.setVisible(true); trueFalseBox.setManaged(true);
            }
        }
    }

    /**
     * Kończy egzamin: zatrzymuje timer, zapisuje wynik i odpowiedzi do bazy,
     * a następnie przełącza scenę na widok wyniku.
     */
    private void finishExam() {
        try {
            if (timer != null) timer.stop();
            LocalDateTime endTime = LocalDateTime.now();

            ExamResult result = new ExamResult(user.getId(), exam.getId(), startTime, endTime, score);

            int examId = ExamResultDAO.saveExam(result);

            for (Answer a : givenAnswers) {
                a.setExamResultId(examId);
                AnswerDAO.saveAnswer(a);
            }

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
