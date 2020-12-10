package com.tiansirk.countryquiz.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

import java.util.List;

/**
 * Model class for Firestore document: Question
 */
public class Question implements Parcelable, Identifiable {

    //The auto ID given by Firestore. It is needed to have during queries for identification, sorting, etc purposes
    private String documentId;
    private int number;
    private String question;
    private String rightAnswer;
    private List<String> wrongAnswers;
    private int earnedPoint;
    private boolean answered;
    private String levelId; // foreign key

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

    public String getLevelId() {
        return levelId;
    }

    public void setLevelId(String levelId) {
        this.levelId = levelId;
    }

    @Override
    public String toString() {
        return "Question{" +
                "documentId='" + documentId + '\'' +
                ", number=" + number +
                ", question='" + question + '\'' +
                ", rightAnswer='" + rightAnswer + '\'' +
                ", wrongAnswers=" + wrongAnswers +
                ", earnedPoint=" + earnedPoint +
                ", answered=" + answered +
                ", levelId='" + levelId + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.documentId);
        dest.writeInt(this.number);
        dest.writeString(this.question);
        dest.writeString(this.rightAnswer);
        dest.writeStringList(this.wrongAnswers);
        dest.writeInt(this.earnedPoint);
        dest.writeByte(this.answered ? (byte) 1 : (byte) 0);
        dest.writeString(this.levelId);
    }

    protected Question(Parcel in) {
        this.documentId = in.readString();
        this.number = in.readInt();
        this.question = in.readString();
        this.rightAnswer = in.readString();
        this.wrongAnswers = in.createStringArrayList();
        this.earnedPoint = in.readInt();
        this.answered = in.readByte() != 0;
        this.levelId = in.readString();
    }

    public static final Parcelable.Creator<Question> CREATOR = new Parcelable.Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel source) {
            return new Question(source);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    //Need to be excluded from Firestore's autogenereting the Object
    @Exclude
    @Override
    public Object getEntityKey() {
        return documentId;
    }
}
