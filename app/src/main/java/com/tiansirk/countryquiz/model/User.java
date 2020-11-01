package com.tiansirk.countryquiz.model;

import com.google.firebase.firestore.Exclude;

import java.util.List;

/**
 * Model class for Firestore document: User
 */
public class User {

    //The auto ID given by Firestore. It is needed to have during queries for identification, sorting, etc purposes
    private String documentId;
    private String username;
    private int totalPoints;
    private List<Level> completedLevels;
    private List<Country> countries;

    /** Empty constructor is needed for Firestore to be able to recreate the Object from its Document */
    public User() {
    }

    public User(String username, int totalPoints, List<Level> completedLevels, List<Country> countries) {
        this.username = username;
        this.totalPoints = totalPoints;
        this.completedLevels = completedLevels;
        this.countries = countries;
    }

    //Need to be excluded from Firestore's autogenereting the Object
    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
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
