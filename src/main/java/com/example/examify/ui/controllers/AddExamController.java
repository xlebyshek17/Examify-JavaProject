package com.example.examify.ui.controllers;

import com.example.examify.model.Exam;
import com.example.examify.model.Question;
import com.example.examify.service.ExamService;
import com.example.examify.service.QuestionService;
import com.example.examify.util.FxErrorHelper;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AddExamController {

    @FXML private Label msgError;
    @FXML Label labelExam;
    @FXML private TableColumn<Question, Void> actionsColumn;
    @FXML private TableView<Question> questionTable;
    @FXML private TableColumn<Question, String> questionTextColumn;
    @FXML private TableColumn<Question, String> correctAnswerColumn;

    @FXML private TextField examName;
    @FXML private TextField timeLimit;
    @FXML private Spinner<Integer> questionCountSpinner;

    @FXML private Button saveButton;

    private final ExamService examService = new ExamService();
    private final QuestionService questionService = new QuestionService();
    private final ObservableList<Question> questionList = FXCollections.observableArrayList();
    private AdminExamsController parentController;

    private Exam editingExam = null;

    @FXML
    public void initialize() {
        saveButton.setDisable(true);
        msgError.setVisible(false);

        ChangeListener<Object> cl = (observableValue, oldValue, newValue) -> {
            try {
                validateAll();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
        examName.textProperty().addListener(cl);
        timeLimit.textProperty().addListener(cl);

        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        questionCountSpinner.setValueFactory(valueFactory);

        questionTextColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
        correctAnswerColumn.setCellValueFactory(new PropertyValueFactory<>("correctAnswer"));
        questionTable.setItems(questionList);
        addActionsToTable();
    }

    private void validateAll() throws SQLException {
        String n = examName.getText();
        String t = timeLimit.getText();
        boolean valid = true;

        // for exam name
        if (editingExam == null && examService.findByTitle(n).isPresent()) {
            FxErrorHelper.showError(msgError, "An exam with this title already exists.", saveButton);
            return;
        }
        if (n.length() <= 1 || n.length() > 100) {
            FxErrorHelper.showError(msgError, "Exam name must be 1-100 characters.", saveButton);
            valid = false;
        } else {
            FxErrorHelper.hideError(msgError);
        }

        // for time limit
        if (t != null && t.matches("\\d+")) {
            if (Integer.parseInt(t) <= 0) {
                FxErrorHelper.showError(msgError, "Time limit must be a positive integer.", saveButton);
                valid = false;
            }
        }
        else {
            FxErrorHelper.showError(msgError, "Please enter a valid number for duration.");
            valid = false;
        }


        if (valid) {
            FxErrorHelper.hideError(msgError, saveButton);
        } else {
            saveButton.setDisable(true);
        }
    }

    @FXML
    public void addQuestion(ActionEvent actionEvent) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/examify/fxml/add_question-view.fxml"));
            Parent root = loader.load();
            AddQuestionController controller = loader.getController();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            Question createdQuestion = controller.getQuestion();
            if (createdQuestion != null) {
                questionList.add(createdQuestion);
                System.out.println("Question from exam add: " + createdQuestion);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void saveExam(ActionEvent actionEvent) throws SQLException, IOException {
        String title = examName.getText();
        int questionCount = questionCountSpinner.getValue();
        int timeLimit = Integer.parseInt(this.timeLimit.getText());

        Exam exam = new Exam();
        exam.setTitle(examName.getText());
        exam.setQuestionCount(questionCount);
        exam.setTimeLimitMinutes(timeLimit);
        int examId;
        if (editingExam == null) {
            examId = examService.saveExam(exam);
        }
        else {
            exam.setId(editingExam.getId());
            examId = examService.updateExam(exam);
        }

        for (Question q: questionList) {
            q.setExamId(examId);
            System.out.println("Question from exam save: " + q);
            questionService.updateOrInsertQuestion(q);
        }

        ((Stage) saveButton.getScene().getWindow()).close();
        parentController.refreshExamTable();
    }

    public void setParentController(AdminExamsController controller) {
        this.parentController = controller;
    }

    public void setEditingExam(Exam editingExam) throws SQLException {
        this.editingExam = editingExam;

        examName.setText(editingExam.getTitle());
        questionCountSpinner.getValueFactory().setValue(editingExam.getQuestionCount());
        timeLimit.setText(String.valueOf(editingExam.getTime_limit_minutes()));

        questionList.setAll(questionService.getQuestionsByExamId(editingExam.getId()));
    }

    private void addActionsToTable() {
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button updateButton = new Button("Update");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttonsBox = new HBox(5, updateButton, deleteButton);

            {
                updateButton.setOnAction(event -> {
                    Question selectedQuestion = getTableView().getItems().get(getIndex());
                    try {
                        handleUpdate(selectedQuestion);

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

                deleteButton.setOnAction(event -> {
                    Question selectedQuestion = getTableView().getItems().get(getIndex());
                    handleDelete(selectedQuestion);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonsBox);
                }
            }
        });
    }

    private void handleDelete(Question selectedQuestion) {
        try {
            boolean success = questionService.deleteQuestion(selectedQuestion.getId());
            if (success) {
                questionList.remove(selectedQuestion);
                questionTable.getItems().remove(selectedQuestion);
                System.out.println("Deleted question " + selectedQuestion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleUpdate(Question selectedQuestion) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/examify/fxml/add_question-view.fxml"));
        Parent newRoot = loader.load();

        AddQuestionController controller = loader.getController();
        controller.setEditingQuestion(selectedQuestion);
        controller.labelQuestion.setText("Edit Question");
        Stage stage = new Stage();
        stage.setScene(new Scene(newRoot));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        Question updatedQuestion = controller.getQuestion();

        if (updatedQuestion != null) {
            updatedQuestion.setExamId(selectedQuestion.getExamId());
            updatedQuestion.setId(selectedQuestion.getId());
            int index = questionList.indexOf(selectedQuestion);
            if (index >= 0) {
                System.out.println("Selected question: " + selectedQuestion);
                System.out.println("Updated question: " + updatedQuestion);

                questionList.set(index, updatedQuestion);
            }
        }
    }
}
