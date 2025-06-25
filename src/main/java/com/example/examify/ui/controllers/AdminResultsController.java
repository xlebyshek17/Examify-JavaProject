package com.example.examify.ui.controllers;

import com.example.examify.model.Exam;
import com.example.examify.model.ExamResult;
import com.example.examify.model.ExamStats;
import com.example.examify.service.ExamResultService;
import com.example.examify.service.ExamService;
import com.example.examify.service.QuestionService;
import com.example.examify.service.UserService;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Kontroler widoku wyników egzaminów dla administratora.
 * Wyświetla statystyki, tabelę wyników i wykres słupkowy.
 */
public class AdminResultsController {
    private final ExamService examService = new ExamService();
    private final ExamResultService examResultService = new ExamResultService();
    private final QuestionService questionService = new QuestionService();
    private final UserService userService = new UserService();
    @FXML private TableView<ExamResult> resultsTable;
    @FXML private TableColumn<ExamResult, String> studentColumn;
    @FXML private TableColumn<ExamResult, Double> scoreColumn;
    @FXML private TableColumn<ExamResult, String> startColumn;
    @FXML private TableColumn<ExamResult, String> endColumn;
    @FXML private TableColumn<ExamResult, String> durationColumn;
    @FXML private BarChart<String, Number> barChart;
    @FXML private GridPane statsGrid;

    @FXML private ComboBox<Exam> examComboBox;
    @FXML private Label attemps;
    @FXML private Label avScore;
    @FXML private Label maxScore;
    @FXML private Label minScore;
    @FXML private Label passPercentage;
    @FXML private Label averageDuration;

    /**
     * Inicjalizuje komponenty: kolumny tabeli, combo box z egzaminami,
     * ustawienia siatki oraz reakcje na wybór egzaminu.
     *
     * @throws SQLException jeśli wystąpi błąd podczas pobierania danych z bazy
     */
     public void initialize() throws SQLException {
         studentColumn.setPrefWidth(180);
         scoreColumn.setPrefWidth(180);
         startColumn.setPrefWidth(250);
         endColumn.setPrefWidth(250);
         durationColumn.setPrefWidth(150);
         examComboBox.setStyle("-fx-font-size: 18px;");
         List<Exam> exams = examService.getAllExams();
         examComboBox.getItems().setAll(exams);

         statsGrid.setHgap(20);
         statsGrid.setVgap(10);

         studentColumn.setCellValueFactory(data -> {
             int userId = data.getValue().getUserId();
             if (userService.getUserById(userId).isPresent()) {
                 String userName = userService.getUserById(userId).get().getUsername();
                 return new SimpleStringProperty(userName);
             }
             return new SimpleStringProperty("Unknown");
         });
         scoreColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getScore()).asObject());
         startColumn.setCellValueFactory(cellData -> {
             LocalDateTime dateTime = cellData.getValue().getStartTime();
             String formatted = dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
             return new ReadOnlyStringWrapper(formatted);
         });
         endColumn.setCellValueFactory(cellData -> {
             LocalDateTime dateTime = cellData.getValue().getEndTime();
             String formatted = dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
             return new ReadOnlyStringWrapper(formatted);
         });
         durationColumn.setCellValueFactory(cellData -> {
             LocalDateTime start = cellData.getValue().getStartTime();
             LocalDateTime end = cellData.getValue().getEndTime();
             Duration duration = Duration.between(start, end);
             double minutes = duration.toMillis() / 60000.0;
             return new ReadOnlyStringWrapper(String.format("%.1f min", minutes));
         });

         examComboBox.setOnAction(event -> {
             Exam selectedExam = examComboBox.getValue();

             if (selectedExam != null) {
                 int examId = selectedExam.getId();
                 Optional<ExamStats> stats = Optional.empty();
                 try {
                     stats = examResultService.getExamStats(examId);
                     List<ExamResult> results = examResultService.getResultsForExam(examId);
                     resultsTable.setItems(FXCollections.observableArrayList(results));
                     showScoreBandsBarChart(examId);
                 } catch (SQLException e) {
                     throw new RuntimeException(e);
                 }

                 stats.ifPresent(s -> {
                     attemps.setText(s.getTotalAttempts() + " ");
                     avScore.setText(String.format("%.2f", s.getAverageScore()));
                     maxScore.setText(String.format("%.2f", s.getMaxScore()));
                     minScore.setText(String.format("%.2f", s.getMinScore()));
                     passPercentage.setText(String.format("%.1f", s.getPassPercentage()) + "%");
                     averageDuration.setText(String.format("%.1f", s.getAverageDurationMinutes()) + " min");
                 });

             }
         });
     }

    /**
     * Wyświetla wykres słupkowy przedstawiający rozkład ocen z danego egzaminu.
     *
     * @param examId ID wybranego egzaminu
     * @throws SQLException jeśli wystąpi błąd podczas pobierania danych z bazy
     */
    public void showScoreBandsBarChart(int examId) throws SQLException {
        int totalQuestions = questionService.getQuestionCountForExam(examId);

        List<ExamResult> results = examResultService.getResultsForExam(examId);

        Map<Integer, Integer> gradeCount = new HashMap<>();

        for (int grade = 2; grade <= 5; grade++) {
            gradeCount.put(grade, 0);
        }


        for (ExamResult result : results) {
            double percent = (result.getScore() * 100.0) / totalQuestions;
            int grade;

            if (percent < 50) {
                grade = 2;
            } else if (percent <= 60) {
                grade = 3;
            } else if (percent <= 80) {
                grade = 4;
            } else {
                grade = 5;
            }

            gradeCount.put(grade, gradeCount.get(grade) + 1);
        }
        barChart.setTitle("Distribution by Grade (based on % score)");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Grades");

        for (int grade = 2; grade <= 5; grade++) {
            series.getData().add(new XYChart.Data<>(String.valueOf(grade), gradeCount.get(grade)));
        }

        barChart.getData().clear();
        barChart.getData().add(series);
    }
}
