package com.example.examify.ui.controllers;

import com.example.examify.model.User;
import com.example.examify.dao.UserDAO;
import com.example.examify.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.util.List;

/**
 * Kontroler widoku zarządzania użytkownikami przez administratora.
 * Umożliwia przeglądanie listy użytkowników oraz nadawanie lub odbieranie uprawnień administratora.
 */
public class AdminUsersController {
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, Boolean> isAdminColumn;
    @FXML private TableColumn<User, Void> actionColumn;

    private final UserService userService = new UserService();

    /**
     * Inicjalizuje kolumny tabeli i ładuje użytkowników.
     */
    @FXML
    public void initialize() {
        usernameColumn.prefWidthProperty().bind(userTable.widthProperty().multiply(0.25));
        emailColumn.prefWidthProperty().bind(userTable.widthProperty().multiply(0.35));
        isAdminColumn.prefWidthProperty().bind(userTable.widthProperty().multiply(0.15));
        actionColumn.prefWidthProperty().bind(userTable.widthProperty().multiply(0.25));

        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        isAdminColumn.setCellValueFactory(new PropertyValueFactory<>("admin"));
        loadUsers();
        addButtonToTable();
    }

    /**
     * Wczytuje listę użytkowników z bazy danych i ustawia w tabeli.
     */
    private void loadUsers() {
        List<User> users = userService.getAllUsers();
        userTable.getItems().setAll(users);
    }

    /**
     * Dodaje przycisk akcji do kolumny, umożliwiający zmianę statusu administratora.
     * Tekst przycisku jest aktualizowany dynamicznie w zależności od bieżącego statusu.
     */
    public void addButtonToTable() {
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button button = new Button();
            {
                button.setPrefWidth(120);
                button.setPrefHeight(28);
                button.setStyle("-fx-font-size: 13px;");

                button.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    boolean newStatus = !user.isAdmin();

                    userService.updateAdminStatus(user.getId(), newStatus);

                    user.setAdmin(newStatus);

                    updateButtonText(user);
                    getTableView().refresh();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    updateButtonText(user);
                    setGraphic(button);
                }
            }

            private void updateButtonText(User user) {
                button.setText(user.isAdmin() ? "remove admin" : "make admin");
            }
        });
    }
}
