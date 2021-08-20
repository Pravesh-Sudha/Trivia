package com.example.trivia.model;

public class Question {
   private String answer;
    private boolean answerTrue;

    public Question(String answer,boolean answerTrue) {
        this.answer = answer;
        this.answerTrue = answerTrue;
    }

    public Question() {
    }

    @Override
    public String toString() {
        return "Question{" +
                "answer='" + answer + '\'' +
                ", answerTrue=" + answerTrue +
                '}';
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean getAnswerTrue() {
        return answerTrue;
    }

    public void setAnswerTrue(boolean answerTrue) {
        this.answerTrue = answerTrue;
    }
}
