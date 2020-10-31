package com.tiansirk.countryquiz.model;

import java.util.List;

public class User {

    private String username;
    private List<Level> completedLevels;
    private List<Country> countries;

    public User(String username, List<Level> completedLevels, List<Country> countries) {
        this.username = username;
        this.completedLevels = completedLevels;
        this.countries = countries;
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

    public List<Country> getCountries() {
        return countries;
    }

    public void setCountries(List<Country> countries) {
        this.countries = countries;
    }
}
