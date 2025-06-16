package com.example.examify.model;

import java.util.*;

public class Question {
    private int id;
    private int examId;
    private String text;
    private QuestionType type;
    private String options;
    private String correctAnswer;

    public Question(int id, int examId, String text, QuestionType type, String options, String correctAnswer) {
        this.id = id;
        this.examId = examId;
        this.text = text;
        this.type = type;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }
    public Question() {}

    public Question(int examId, String text, QuestionType type, String options, String correctAnswer) {
        this(0, examId, text, type, options, correctAnswer);
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
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

    public QuestionType getType() {
        return type;
    }

    public void setType(QuestionType type) {
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

    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

//    /** Pierwsza odpowiedź traktowana jest jako poprawna */
//    public String getCorrectOption() {
//        return getOptionList().get(0);
//    }
}
