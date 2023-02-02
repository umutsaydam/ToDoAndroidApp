package com.example.todoapp.Models;

public class ChallengeModel {
    String id;
    String challengeTitle;
    String challengeCategory;
    int challengeDay;
    String challengeDescription;

    public ChallengeModel() {
    }

    public ChallengeModel(String id, String challengeTitle, String challengeCategory, int challengeDay, String challengeDescription) {
        this.id = id;
        this.challengeTitle = challengeTitle;
        this.challengeCategory = challengeCategory;
        this.challengeDay = challengeDay;
        this.challengeDescription = challengeDescription;
    }

    public String getId() {
        return id;
    }

    public String getChallengeTitle() {
        return challengeTitle;
    }

    public String getChallengeCategory() {
        return challengeCategory;
    }

    public int getChallengeDay() {
        return challengeDay;
    }

    public String getChallengeDescription() {
        return challengeDescription;
    }
}
