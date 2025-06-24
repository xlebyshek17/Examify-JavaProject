package com.example.examify.model;

public class ExamStats {
    private int totalAttempts;
    private double averageScore;
    private double maxScore;
    private double minScore;
    private double passPercentage;
    private double averageDurationMinutes;

    public ExamStats(int totalAttempts, double averageScore, double maxScore, double minScore,
                     double passPercentage, double averageDurationMinutes) {
        this.totalAttempts = totalAttempts;
        this.averageScore = averageScore;
        this.maxScore = maxScore;
        this.minScore = minScore;
        this.passPercentage = passPercentage;
        this.averageDurationMinutes = averageDurationMinutes;
    }

    public int getTotalAttempts() {
        return totalAttempts;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public double getMaxScore() {
        return maxScore;
    }

    public double getMinScore() {
        return minScore;
    }

    public double getPassPercentage() {
        return passPercentage;
    }

    public double getAverageDurationMinutes() {
        return averageDurationMinutes;
    }

    @Override
    public String toString() {
        return String.format(
                "Attempts: %d\nAvg: %.2f\nMax: %.2f\nMin: %.2f\nPass %%: %.1f%%\nAvg Time: %.1f min",
                totalAttempts, averageScore, maxScore, minScore, passPercentage, averageDurationMinutes
        );
    }
}

