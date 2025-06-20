package com.example.examify.ui.controllers;

import com.example.examify.dao.ExamDAO;
import com.example.examify.model.Exam;
import com.example.examify.model.Question;
import com.example.examify.service.ExamService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class AdminExamsController {
    @FXML private TableColumn<Exam, Integer> timeLimitColumn;
    @FXML private TableColumn<Exam, String> examNameColumn;
    @FXML private TableColumn<Exam, LocalDateTime> dateColumn;
    @FXML private TableColumn<Exam, Void> actionsColumn;
    @FXML private TableView<Exam> examTable;

    private final ExamService examService = new ExamService();

    @FXML
    public void initialize() throws SQLException {
        examNameColumn.prefWidthProperty().bind(examTable.widthProperty().multiply(0.30));
        dateColumn.prefWidthProperty().bind(examTable.widthProperty().multiply(0.25));
        timeLimitColumn.prefWidthProperty().bind(examTable.widthProperty().multiply(0.25));
        actionsColumn.prefWidthProperty().bind(examTable.widthProperty().multiply(0.20));

        examNameColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        timeLimitColumn.setCellValueFactory(new PropertyValueFactory<>("time_limit_minutes"));
        refreshExamTable();

        addActionsToTable();
    }

    private void handleDelete(Exam selectedExam) {
        try {
            boolean success = examService.deleteExam(selectedExam.getId());
            if (success) {
                examTable.getItems().remove(selectedExam);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleUpdate(Exam selectedExam) throws SQLException, IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/examify/fxml/add_exam-view.fxml"));
        Parent newRoot = loader.load();

        AddExamController addExamController = loader.getController();
        addExamController.setParentController(this);
        addExamController.setEditingExam(selectedExam);
        addExamController.labelExam.setText("Edit Exam");
        Stage stage = new Stage();
        stage.setScene(new Scene(newRoot));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    @FXML
    public void addExam(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/examify/fxml/add_exam-view.fxml"));
        Parent newRoot = loader.load();

        AddExamController addExamController = loader.getController();
        addExamController.setParentController(this);

        Stage stage = new Stage();
        stage.setScene(new Scene(newRoot));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    public void refreshExamTable() throws SQLException {
        List<Exam> exams = examService.getAllExams();
        examTable.setItems(FXCollections.observableArrayList(exams));
    }

    private void addActionsToTable() {
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button updateButton = new Button("Update");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttonsBox = new HBox(5, updateButton, deleteButton);

            {
                updateButton.setOnAction(event -> {
                    Exam selectedExam = getTableView().getItems().get(getIndex());
                    try {
                        handleUpdate(selectedExam);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

                deleteButton.setOnAction(event -> {
                    Exam selectedExam = getTableView().getItems().get(getIndex());
                    handleDelete(selectedExam);
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
}
