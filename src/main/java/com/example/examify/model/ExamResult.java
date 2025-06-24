package com.example.examify.model;

import java.time.LocalDateTime;

public class ExamResult {
    private int id;
    private int examId;
    private int userId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double score;

    public ExamResult(int id, int studentId, int examId, LocalDateTime startTime, LocalDateTime endTime, double score) {
        this.id = id;
        this.userId = studentId;
        this.examId = examId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.score = score;
    }

    public ExamResult(int studentId, int examId, LocalDateTime startTime, LocalDateTime endTime, double score) {
        this(0, studentId, examId, startTime, endTime, score);
    }

    public ExamResult() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUsertId(int studentId) {
        this.userId = studentId;
    }

    public int getExamId() { return examId; }

    public void setExamId(int examId) { this.examId = examId; }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Exam{" +
                "id=" + id +
                ", studentId=" + userId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", score=" + score +
                '}';
    }
}
