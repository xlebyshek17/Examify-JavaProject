package com.example.examify;

import com.example.examify.util.DBUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        DBUtil.init();
        // Ładujemy plik FXML z widokiem logowania
        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/com/example/examify/fxml/welcome-view.fxml")
        );
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Examify — Welcome");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}