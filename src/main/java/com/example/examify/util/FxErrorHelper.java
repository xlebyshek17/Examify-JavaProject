package com.example.examify.util;

import javafx.scene.Node;
import javafx.scene.control.Label;

public class FxErrorHelper {
    public static void showError(Label label, String message, Node... controlsToDisable) {
        label.setText(message);
        label.setVisible(true);
        for (Node control : controlsToDisable) {
            control.setDisable(true);
        }
    }

    public static void hideError(Label label, Node... controlsToEnable) {
        label.setText("");
        label.setVisible(false);
        for (Node control : controlsToEnable) {
            control.setDisable(false);
        }
    }
}
