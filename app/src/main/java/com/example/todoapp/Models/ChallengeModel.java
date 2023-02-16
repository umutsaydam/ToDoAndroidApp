package com.example.todoapp.Models;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ChallengeModel {
    String id;
    String challengeTitle;
    String challengeCategory;
    String startDay;
    String endDay;
    String challengeDescription;
    int challengeDay;
    List<Boolean> challangeStatus;

    public ChallengeModel() {
    }

    public ChallengeModel(String id, String challengeTitle, String challengeCategory, String startDay, String endDay, String challengeDescription) {
        this.id = id;
        this.challengeTitle = challengeTitle;
        this.challengeCategory = challengeCategory;
        this.startDay = startDay;
        this.endDay = endDay;
        this.challengeDescription = challengeDescription;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date s = sdf.parse(startDay);
            Date e = sdf.parse(endDay);
            long diff = e.getTime() - s.getTime();
            this.challengeDay = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            Boolean [] tmp = new Boolean[challengeDay];
            Arrays.fill(tmp, false);
            this.challangeStatus = new ArrayList<Boolean>(Arrays.asList(tmp));
            System.out.println(this.challengeDay);
        } catch (ParseException e) {
            System.out.println(e.getMessage()+" try*****");
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChallengeTitle() {
        return challengeTitle;
    }

    public void setChallengeTitle(String challengeTitle) {
        this.challengeTitle = challengeTitle;
    }

    public String getChallengeCategory() {
        return challengeCategory;
    }

    public void setChallengeCategory(String challengeCategory) {
        this.challengeCategory = challengeCategory;
    }

    public String getStartDay() {
        return startDay;
    }

    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }

    public String getEndDay() {
        return endDay;
    }

    public void setEndDay(String endDay) {
        this.endDay = endDay;
    }

    public String getChallengeDescription() {
        return challengeDescription;
    }

    public void setChallengeDescription(String challengeDescription) {
        this.challengeDescription = challengeDescription;
    }

    public List<Boolean> getChallangeStatus() {
        return challangeStatus;
    }

    public void setChallangeStatus(List<Boolean> challangeStatus) {
        this.challangeStatus = challangeStatus;
    }
}
