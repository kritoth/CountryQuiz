package com.tiansirk.countryquiz.model;

import com.google.firebase.firestore.Exclude;

import java.util.List;

/**
 * Model class for Firestore document: Question
 */
public class Question {

    //The auto ID given by Firestore. It is needed to have during queries for identification, sorting, etc purposes
    private String documentId;
    private int number;
    private String question;
    private String rightAnswer;
    private List<String> wrongAnswers;
    private int earnedPoint;
    private boolean answered;

    /** Empty constructor is needed for Firestore to be able to recreate the Object from its Document */
    public Question() {
    }

    public Question(int number, String question, String rightAnswer, List<String> wrongAnswers) {
        this.number = number;
        this.question = question;
        this.rightAnswer = rightAnswer;
        this.wrongAnswers = wrongAnswers;
        this.earnedPoint = 0;
        this.answered = false;
    }

    //Need to be excluded from Firestore's autogenereting the Object
    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getRightAnswer() {
        return rightAnswer;
    }

    public void setRightAnswer(String rightAnswer) {
        this.rightAnswer = rightAnswer;
    }

    public List<String> getWrongAnswers() {
        return wrongAnswers;
    }

    public void setWrongAnswers(List<String> wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }

    public int getEarnedPoint() {
        return earnedPoint;
    }

    public void setEarnedPoint(int earnedPoint) {
        this.earnedPoint = earnedPoint;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }
}
