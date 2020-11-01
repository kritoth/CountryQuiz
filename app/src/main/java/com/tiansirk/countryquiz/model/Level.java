package com.tiansirk.countryquiz.model;

import com.google.firebase.firestore.Exclude;

import java.util.List;

/**
 * Model class for Firestore document: Level
 */
public class Level {

    //The auto ID given by Firestore. It is needed to have during queries for identification, sorting, etc purposes
    private String documentId;
    private int level;
    private List<Question> questions;
    private int achievedPoints;

    /** Empty constructor is needed for Firestore to be able to recreate the Object from its Document */
    public Level() {
    }

    public Level(int level, List<Question> questions, int achievedPoints) {
        this.level = level;
        this.questions = questions;
        this.achievedPoints = achievedPoints;
    }

    //Need to be excluded from Firestore's autogenereting the Object
    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public int getAchievedPoints() {
        return achievedPoints;
    }

    public void setAchievedPoints(int achievedPoints) {
        this.achievedPoints = achievedPoints;
    }
}
