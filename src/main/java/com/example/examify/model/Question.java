package com.example.examify.model;

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
}
