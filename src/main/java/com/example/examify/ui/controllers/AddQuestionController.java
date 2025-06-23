package com.example.examify.ui.controllers;

import com.example.examify.model.AnswerOption;
import com.example.examify.model.Exam;
import com.example.examify.model.Question;
import com.example.examify.model.QuestionType;
import com.example.examify.service.QuestionService;
import com.example.examify.util.FxErrorHelper;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class AddQuestionController {

    @FXML private Button cancelButton;
    @FXML private Label msgError;
    @FXML Label labelQuestion;
    @FXML private Button saveButton;
    // for true_false questions
    @FXML private VBox trueFalseBox;
    @FXML private RadioButton trueRadio;
    @FXML private RadioButton falseRadio;
    @FXML private final ToggleGroup trueFalseToggleGroup = new ToggleGroup();

    // for single answer questions
    @FXML private HBox optionsBox;
    @FXML private TableColumn<AnswerOption, Void> deleteColumn;
    @FXML private TableColumn<AnswerOption, String> optionColumn;
    @FXML private TableColumn<AnswerOption, Void> correctColumn;
    @FXML private TableView<AnswerOption> optionsTable;
    @FXML private TextArea optionField;

    private final StringBuilder optionBuilder = new StringBuilder();
    private final ObservableList<AnswerOption> optionList = FXCollections.observableArrayList();
    private final ToggleGroup toggleGroup = new ToggleGroup();

    // for short answer questions
    @FXML private HBox shortAnswerBox;
    @FXML private TextField shortAnswerField;

    @FXML private TextArea questionText;
    @FXML private ComboBox<QuestionType> questionTypeComboBox;
    private String correctAnswer = null;

    private Question q;

    @FXML
    private void initialize() {
        saveButton.setDisable(true);
        msgError.setVisible(false);

        ChangeListener<Object> cl = (observableValue, oldValue, newValue) -> validateAll();
        questionText.textProperty().addListener(cl); // title of Q
        optionField.textProperty().addListener(cl); // for single choice answer
        questionTypeComboBox.valueProperty().addListener(cl); // type of Q
        trueFalseToggleGroup.selectedToggleProperty().addListener(cl);
        toggleGroup.selectedToggleProperty().addListener(cl);
        shortAnswerField.textProperty().addListener(cl);

        trueRadio.setToggleGroup(trueFalseToggleGroup);
        falseRadio.setToggleGroup(trueFalseToggleGroup);

        setTypeComboBox();
        setOptionsTable();
    }

    private void setTypeComboBox() {
        questionTypeComboBox.setItems(FXCollections.observableArrayList(QuestionType.values()));

        questionTypeComboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(QuestionType type, boolean empty) {
                super.updateItem(type, empty);
                setText(empty || type == null ? null : type.getLabel());
            }
        });

        questionTypeComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(QuestionType type, boolean empty) {
                super.updateItem(type, empty);
                setText(empty || type == null ? null : type.getLabel());
            }
        });

        questionTypeComboBox.valueProperty().addListener((obs, oldType, newType) -> {
            updateUIByQuestionType(newType);
        });
    }

    private void setOptionsTable() {
        optionColumn.setCellValueFactory(data -> data.getValue().textProperty());
        optionsTable.setItems(optionList);

        correctColumn.setCellFactory(col -> new TableCell<>() {
            private final RadioButton radioButton = new RadioButton();

            {
                radioButton.setToggleGroup(toggleGroup);
                radioButton.setOnAction(event -> {
                    AnswerOption selected = getTableView().getItems().get(getIndex());
                    correctAnswer = selected.getText();
                    validateAll();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    AnswerOption current = getTableView().getItems().get(getIndex());
                    radioButton.setSelected(current.getText().equals(correctAnswer));
                    setGraphic(radioButton);
                }
            }
        });

        deleteColumn.setCellFactory(col -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(event -> {
                    AnswerOption option = getTableView().getItems().get(getIndex());
                    optionList.remove(option);

                    int start = optionBuilder.indexOf(option.getText());
                    int end = optionBuilder.indexOf(";", start);
                    if (start != -1 && end != -1) {
                        optionBuilder.delete(start, end + 1);
                    } else if (start != -1) {
                        optionBuilder.delete(start, start + option.getText().length());
                    }
                    validateAll();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
    }

    public void validateAll() {
        String q = questionText.getText();
        QuestionType type = questionTypeComboBox.getValue();
        boolean valid = true;

        // Title of the question
        if (q.isEmpty() || (q.length() < 5 || q.length() > 200)) {
            FxErrorHelper.showError(msgError, "Text of the question must be 5-200 characters.", saveButton);
            valid = false;
        } else {
            FxErrorHelper.hideError(msgError);
        }

        // For question type
        if (type == null) {
            FxErrorHelper.showError(msgError, "Please select a question type.", saveButton);
            valid = false;
        } else {
            // for correct answer
            switch (type) {
                case TRUE_FALSE -> {
                    if (trueFalseToggleGroup.getSelectedToggle() == null) {
                        FxErrorHelper.showError(msgError, "Please select TRUE or FALSE.", saveButton);
                        valid = false;
                    }
                }
                case SINGLE_CHOICE -> {
                    if (optionList.isEmpty()) {
                        FxErrorHelper.showError(msgError, "Please add at least one answer option.", saveButton);
                        valid = false;
                    }
                    if (!optionBuilder.isEmpty() && (correctAnswer == null || correctAnswer.trim().isEmpty())) {
                        FxErrorHelper.showError(msgError, "Please select the correct answer.", saveButton);
                        valid = false;
                    }
                }
                case SHORT_ANSWER -> {
                    if (shortAnswerField.getText().trim().isEmpty()) {
                        FxErrorHelper.showError(msgError, "Please provide the correct short answer.", saveButton);
                        valid = false;
                    }
                }
            }

        }

        if (valid) {
            FxErrorHelper.hideError(msgError, saveButton);
        } else {
            saveButton.setDisable(true);
        }
    }

    private void updateUIByQuestionType(QuestionType type) {
        optionsBox.setVisible(false);
        optionsBox.setManaged(false);
        shortAnswerBox.setVisible(false);
        shortAnswerBox.setManaged(false);
        trueFalseBox.setVisible(false);
        trueFalseBox.setManaged(false);

        if (type == QuestionType.SINGLE_CHOICE) {
            optionsBox.setVisible(true);
            optionsBox.setManaged(true);
        } else if (type == QuestionType.SHORT_ANSWER) {
            shortAnswerBox.setVisible(true);
            shortAnswerBox.setManaged(true);
        } else if (type == QuestionType.TRUE_FALSE) {
            trueFalseBox.setVisible(true);
            trueFalseBox.setManaged(true);
        }
    }

    @FXML
    public void hadleAddOption(ActionEvent actionEvent) {
        String text = optionField.getText();

        if (text.isEmpty() || (text.length() < 5 || text.length() > 100)) {
            FxErrorHelper.showError(msgError, "Option for the question must be 5-100 characters.");
        } else {
            optionList.add(new AnswerOption(text));
            optionsTable.refresh();

            if (!optionBuilder.isEmpty()) {
                optionBuilder.append(";");
            }
            optionBuilder.append(text);
            optionField.clear();
        }
    }

    @FXML
    public void handleSaveButton(ActionEvent actionEvent) throws SQLException {
        String question = questionText.getText();
        QuestionType type = questionTypeComboBox.getValue();

        q = new Question();
        q.setText(question);
        q.setType(type);

        if (type == QuestionType.SINGLE_CHOICE) {
            q.setOptions(optionBuilder.toString());
            q.setCorrectAnswer(correctAnswer);
            System.out.println(optionBuilder);
            System.out.println("Saved question single choice ");
        }

        else if (type == QuestionType.TRUE_FALSE) {
            q.setOptions("True;False");
            if (trueRadio.isSelected()) {
                q.setCorrectAnswer("True");
            } else if (falseRadio.isSelected()) {
                q.setCorrectAnswer("False");
            }

            System.out.println("Saved question true false");
        }

        else if (type == QuestionType.SHORT_ANSWER) {
            q.setOptions("");
            q.setCorrectAnswer(shortAnswerField.getText());

            System.out.println("Saved question short answer");
        }

        ((Stage) saveButton.getScene().getWindow()).close();
    }

    public Question getQuestion() {
        System.out.println("Question from ques " + q);
        return q; }

    public void setEditingQuestion(Question selectedQuestion) {
        questionText.setText(selectedQuestion.getText());
        questionTypeComboBox.setValue(selectedQuestion.getType());
        updateUIByQuestionType(selectedQuestion.getType());

        if (selectedQuestion.getType() == QuestionType.SINGLE_CHOICE) {
            optionBuilder.append(selectedQuestion.getOptions());
            optionList.setAll(selectedQuestion.getAnswerOptionList());
            this.correctAnswer = selectedQuestion.getCorrectAnswer();
        }

        else if (selectedQuestion.getType() == QuestionType.TRUE_FALSE) {
            if ("true".equalsIgnoreCase(selectedQuestion.getCorrectAnswer())) {
                trueRadio.setSelected(true);
            } else {
                falseRadio.setSelected(true);
            }
        }
        else {
            shortAnswerField.setText(selectedQuestion.getCorrectAnswer());
        }
    }

    @FXML
    public void handleCancelButton(ActionEvent actionEvent) {
        q = null;
        ((Stage) cancelButton.getScene().getWindow()).close();
    }
}
