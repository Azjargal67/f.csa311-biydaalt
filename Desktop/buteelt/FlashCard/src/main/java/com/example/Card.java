package com.example;

public class Card {
    private String question;
    private String answer;
    private int correctCount;
    private int totalAttempts;
    private boolean lastAttemptCorrect;

    public Card(String question, String answer) {
        this.question = question;
        this.answer = answer;
        this.correctCount = 0;
        this.totalAttempts = 0;
        this.lastAttemptCorrect = true;
    }

    public String getQuestion() { return question; }
    public String getAnswer() { return answer; }
    public int getCorrectCount() { return correctCount; }
    public int getTotalAttempts() { return totalAttempts; }
    public boolean wasLastAttemptCorrect() { return lastAttemptCorrect; }

    public void recordAttempt(boolean correct) {
        totalAttempts++;
        if (correct) {
            correctCount++;
        }
        lastAttemptCorrect = correct;
    }
}
