package com.tiansirk.countryquiz.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class for Firestore document: User
 */
public class User implements Parcelable, Identifiable {

    private String documentId;  // equals to FirebaseAuth's uId
    private String username;
    private int totalPoints;
    private List<Integer> completedLevels;

    public static User newInstance() {
        return new User();
    }

    /** Empty constructor is needed for Firestore to be able to recreate the Object from its Document */
    public User() {
    }

    public User(String documentId, String username, int totalPoints, List<Integer> completedLevels) {
        this.documentId = documentId;
        this.username = username;
        this.totalPoints = totalPoints;
        this.completedLevels = completedLevels;
    }

    public User(String documentId, String username) {
        this.documentId = documentId;
        this.username = username;
        this.totalPoints = 0;
        this.completedLevels = new ArrayList<>();
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
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

    public List<Integer> getCompletedLevels() {
        return completedLevels;
    }

    public void setCompletedLevels(List<Integer> completedLevels) {
        this.completedLevels = completedLevels;
    }

    @Override
    public String toString() {
        return "User{" +
                "documentId='" + documentId + '\'' +
                ", username='" + username + '\'' +
                ", totalPoints=" + totalPoints +
                ", completedLevels=" + completedLevels +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.documentId);
        dest.writeString(this.username);
        dest.writeInt(this.totalPoints);
        dest.writeList(this.completedLevels);
    }

    protected User(Parcel in) {
        this.documentId = in.readString();
        this.username = in.readString();
        this.totalPoints = in.readInt();
        this.completedLevels = new ArrayList<Integer>();
        in.readList(this.completedLevels, Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Exclude
    @Override
    public Object getEntityKey() {
        return documentId;
    }
}
