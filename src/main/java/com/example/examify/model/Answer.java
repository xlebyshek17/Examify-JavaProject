package com.example.examify.model;

public class Answer {
    private int id;
    private int examResultId;
    private int questionId;
    private String answer;
    private boolean isCorrect;
    private Question question;

    public Answer(int id, int examResultId, int questionId, String answer, boolean isCorrect) {
        this.id = id;
        this.examResultId = examResultId;
        this.questionId = questionId;
        this.answer = answer;
        this.isCorrect = isCorrect;
    }

    public Answer(int examResultId, int questionId, String answer, boolean isCorrect) {
        this(0, examResultId, questionId, answer, isCorrect);
    }


    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getExamResultId() {
        return examResultId;
    }

    public void setExamResultId(int examResultId) {
        this.examResultId = examResultId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }
}
