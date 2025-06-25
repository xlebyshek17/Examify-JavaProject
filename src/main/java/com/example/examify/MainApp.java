package com.example.examify;

import com.example.examify.util.DBUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Główna klasa aplikacji Examify uruchamiana jako aplikacja JavaFX.
 * Inicjalizuje połączenie z bazą danych i wyświetla ekran powitalny.
 */
public class MainApp extends Application {
    /**
     * Punkt wejścia JavaFX. Ładuje widok logowania i uruchamia aplikację.
     *
     * @param stage główna scena aplikacji
     * @throws IOException jeśli nie uda się załadować pliku FXML
     */
    @Override
    public void start(Stage stage) throws IOException {
        System.setProperty("db.mode", "prod");
        DBUtil.init();

        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/com/example/examify/fxml/welcome-view.fxml")
        );
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Examify — Welcome");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Metoda główna uruchamiająca aplikację.
     *
     * @param args argumenty wiersza poleceń
     */
    public static void main(String[] args) {
        launch();
    }
}