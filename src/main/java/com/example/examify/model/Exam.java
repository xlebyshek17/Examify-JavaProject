package com.example.examify.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Exam {
    private int id;
    private String title;
    private int questionCount;
    private int timeLimitMinutes;
    private Timestamp createdAt;

    public Exam() {}

    public Exam(int id, String title, int questionCount, int timeLimitMinutes, Timestamp createdAt) {
        this.id = id;
        this.title = title;
        this.questionCount = questionCount;
        this.timeLimitMinutes = timeLimitMinutes;
        this.createdAt = createdAt;
    }

    public Exam(String title, int questionCount, int timeLimitMinutes, Timestamp createdAt) {
        this(0, title, timeLimitMinutes, questionCount, createdAt);
    }

    public Exam(String title, int questionCount, int timeLimitMinutes) {
        this.title = title;
        this.questionCount = questionCount;
        this.timeLimitMinutes = timeLimitMinutes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public int getTime_limit_minutes() {
        return timeLimitMinutes;
    }

    public void setTimeLimitMinutes(int timeLimitMinutes) {
        this.timeLimitMinutes = timeLimitMinutes;
    }

    @Override
    public String toString() {
        return title;
    }
}
