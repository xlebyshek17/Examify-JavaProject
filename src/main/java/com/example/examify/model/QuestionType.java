package com.example.examify.model;

public enum QuestionType {
    SINGLE_CHOICE("SINGLE_CHOICE"),
    SHORT_ANSWER("SHORT_ANSWER"),
    TRUE_FALSE("TRUE_FALSE");

    private final String label;

    QuestionType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static QuestionType fromLabel(String label) {
        for (QuestionType type : values()) {
            if (type.getLabel().equalsIgnoreCase(label.trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException("No QuestionType with label: " + label);
    }
}
