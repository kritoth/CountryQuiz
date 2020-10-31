package com.tiansirk.countryquiz.model;

import java.util.List;

/**
 * Model class for Firestore document: Level
 */
public class Level {

    private int level;
    private List<Question> questions;

    public Level(int level, List<Question> questions) {
        this.level = level;
        this.questions = questions;
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
}
