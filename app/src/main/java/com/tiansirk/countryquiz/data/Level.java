package com.tiansirk.countryquiz.data;

import java.util.List;

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
