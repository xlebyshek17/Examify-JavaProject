package com.example.examify.ui.controllers;

import com.example.examify.model.Question;
import com.example.examify.model.QuestionType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

public class AddQuestionController {
    @FXML private TableColumn<String, String> optionColumn;
    @FXML private TableColumn<String, Boolean> correctColumn;
    @FXML private TableView<String> optionsTable;
    @FXML private TextField optionField;

    @FXML private Button addOptionButton;

    @FXML private TextField shortAnswerField;

    @FXML private TextField questionText;
    @FXML private ComboBox<QuestionType> questionTypeComboBox;
    @FXML private HBox optionsBox;
    @FXML private HBox shortAnswerBox;

    private final StringBuilder optionBuilder = new StringBuilder();
    private final ObservableList<String> optionList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        questionTypeComboBox.setItems(FXCollections.observableArrayList(QuestionType.values()));

        questionTypeComboBox.valueProperty().addListener((obs, oldType, newType) -> {
            updateUIByQuestionType(newType);
        });

        optionColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        optionsTable.setItems(optionList);
    }

    private void updateUIByQuestionType(QuestionType type) {
        optionsBox.setVisible(false);
        optionsBox.setManaged(false);
        shortAnswerBox.setVisible(false);
        shortAnswerBox.setManaged(false);

        if (type == QuestionType.SINGLE_CHOICE || type == QuestionType.MULTIPLE_CHOICE) {
            optionsBox.setVisible(true);
            optionsBox.setManaged(true);
        } else if (type == QuestionType.SHORT_ANSWER || type == QuestionType.TRUE_FALSE) {
            shortAnswerBox.setVisible(true);
            shortAnswerBox.setManaged(true);
        }
    }

    @FXML
    public void hadleAddOption(ActionEvent actionEvent) {
        String text = optionField.getText();
        if (text.isEmpty()) return;

        optionList.add(text);
        optionsTable.refresh();

        if (optionBuilder.length() > 0) {
            optionBuilder.append(" | "); // разделитель
        }
        optionBuilder.append(text);

        optionField.clear();
    }

    public void handleSaveButton(ActionEvent actionEvent) {
        String question = questionText.getText();
        QuestionType type = questionTypeComboBox.getValue();

        Question q = new Question();
        q.setText(question);
        q.setType(type);
        q.setOptions(optionBuilder.toString());

    }
}
