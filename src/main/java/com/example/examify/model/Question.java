package com.example.examify.model;

import java.util.*;

public class Question {
    private int id;
    private String text;
    private String type;
    private String options;

    public Question(int id, String text, String type, String options) {
        this.id = id;
        this.text = text;
        this.type = type;
        this.options = options;
    }

    public Question(String text, String type, String options) {
        this(0, text, type, options);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public List<String> getOptionList() {
        return Arrays.asList(options.split(";"));   // ["A","B","C","D"]
    }

    /** Pierwsza odpowiedź traktowana jest jako poprawna */
    public String getCorrectOption() {
        return getOptionList().get(0);
    }
}
