package com.example.examify.model;

public enum QuestionType {
    SINGLE_CHOICE,
    MULTIPLE_CHOICE,
    SHORT_ANSWER,
    TRUE_FALSE;

    @Override
    public String toString() {
        return switch (this) {
            case SINGLE_CHOICE -> "Single Choice";
            case MULTIPLE_CHOICE -> "Multiple Choice";
            case SHORT_ANSWER   -> "Short Answer";
            case TRUE_FALSE     -> "True / False";
        };
    }
}
