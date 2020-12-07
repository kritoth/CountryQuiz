package com.tiansirk.countryquiz.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

import java.util.List;

/**
 * Model class for Firestore document: Level
 */
public class Level implements Parcelable, Identifiable {

    //The auto ID given by Firestore. It is needed to have during queries for identification, sorting, etc purposes
    private String documentId;
    private int level;
    private List<Question> questions;
    private int achievedPoints;
    private boolean completed;
    private String userId; //foreign-key

    /** Empty constructor is needed for Firestore to be able to recreate the Object from its Document */
    public Level() {
    }

    public Level(int level, List<Question> questions, int achievedPoints, boolean completed) {
        this.level = level;
        this.questions = questions;
        this.achievedPoints = achievedPoints;
        this.completed = completed;
    }

    //Need to be excluded from Firestore's autogenereting the Object
    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
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

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Level{" +
                "documentId='" + documentId + '\'' +
                ", level=" + level +
                ", questions=" + questions +
                ", achievedPoints=" + achievedPoints +
                ", completed=" + completed +
                ", userId='" + userId + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.documentId);
        dest.writeInt(this.level);
        dest.writeTypedList(this.questions);
        dest.writeInt(this.achievedPoints);
        dest.writeByte(this.completed ? (byte) 1 : (byte) 0);
    }

    protected Level(Parcel in) {
        this.documentId = in.readString();
        this.level = in.readInt();
        this.questions = in.createTypedArrayList(Question.CREATOR);
        this.achievedPoints = in.readInt();
        this.completed = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Level> CREATOR = new Parcelable.Creator<Level>() {
        @Override
        public Level createFromParcel(Parcel source) {
            return new Level(source);
        }

        @Override
        public Level[] newArray(int size) {
            return new Level[size];
        }
    };

    @Exclude
    @Override
    public Object getEntityKey() {
        return  documentId;
    }
}
