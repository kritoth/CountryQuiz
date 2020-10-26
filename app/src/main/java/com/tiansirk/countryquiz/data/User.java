package com.tiansirk.countryquiz.data;

import java.util.List;

public class User {

    private String username;
    private List<Level> completedLevels;

    public User(String username, List<Level> completedLevels) {
        this.username = username;
        this.completedLevels = completedLevels;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Level> getCompletedLevels() {
        return completedLevels;
    }

    public void setCompletedLevels(List<Level> completedLevels) {
        this.completedLevels = completedLevels;
    }
}
