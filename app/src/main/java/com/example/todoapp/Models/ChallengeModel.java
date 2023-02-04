package com.example.todoapp.Models;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChallengeModel {
    String id;
    String challengeTitle;
    String challengeCategory;
    int challengeDay;
    String challengeDescription;
    List<Boolean> challangeStatus;

    public ChallengeModel() {
    }

    public ChallengeModel(String id, String challengeTitle, String challengeCategory, int challengeDay, String challengeDescription) {
        this.id = id;
        this.challengeTitle = challengeTitle;
        this.challengeCategory = challengeCategory;
        this.challengeDay = challengeDay;
        this.challengeDescription = challengeDescription;
        Boolean [] tmp = new Boolean[challengeDay];
        Arrays.fill(tmp, false);
        this.challangeStatus = new ArrayList<Boolean>(Arrays.asList(tmp));

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

    public List<Boolean> getChallangeStatus() {
        return challangeStatus;
    }

    public void setChallangeStatus(List<Boolean> challangeStatus) {
        this.challangeStatus = challangeStatus;
    }
}
