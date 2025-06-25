package com.example.examify.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Reprezentuje pojedynczą opcję odpowiedzi w pytaniu zamkniętym.
 */
public class AnswerOption {
    private final SimpleStringProperty text = new SimpleStringProperty();

    public AnswerOption(String text) { this.text.set(text); }

    public String getText() { return text.get(); }
    public void setText(String text) { this.text.set(text); }
    public StringProperty textProperty() { return text; }
}
