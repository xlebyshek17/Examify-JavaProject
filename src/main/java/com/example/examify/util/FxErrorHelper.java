package com.example.examify.util;

import javafx.scene.Node;
import javafx.scene.control.Label;

/**
 * Klasa pomocnicza do zarządzania komunikatami błędów w komponentach JavaFX.
 */
public class FxErrorHelper {
    /**
     * Pokazuje komunikat błędu i dezaktywuje podane kontrolki.
     *
     * @param label etykieta, w której pokazany zostanie błąd
     * @param message treść błędu
     * @param controlsToDisable kontrolki do dezaktywacji
     */
    public static void showError(Label label, String message, Node... controlsToDisable) {
        label.setText(message);
        label.setVisible(true);
        for (Node control : controlsToDisable) {
            control.setDisable(true);
        }
    }

    /**
     * Ukrywa komunikat błędu i aktywuje podane kontrolki.
     *
     * @param label etykieta do ukrycia
     * @param controlsToEnable kontrolki do aktywacji
     */
    public static void hideError(Label label, Node... controlsToEnable) {
        label.setText("");
        label.setVisible(false);
        for (Node control : controlsToEnable) {
            control.setDisable(false);
        }
    }
}
