package com.example.todoapp.Models;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ChallengeModel {
   private String id;
   private String challengeTitle;
   private String challengeCategory;
   private String challengeStartDay;
   private String challengeEndDay;
   private String challengeDescription;
   private int challengeDay;
   private List<Boolean> challangeStatus;

    public ChallengeModel() {
    }

    public ChallengeModel(String id, String challengeTitle, String challengeCategory, String startDay, String endDay, String challengeDescription) {
        this.id = id;
        this.challengeTitle = challengeTitle;
        this.challengeCategory = challengeCategory;
        this.challengeStartDay = startDay;
        this.challengeEndDay = endDay;
        this.challengeDescription = challengeDescription;
        setStatus();
    }

    private void setStatus() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date s = sdf.parse(challengeStartDay);
            Date e = sdf.parse(challengeEndDay);
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

    public String getChallengeStartDay() {
        return challengeStartDay;
    }

    public void setChallengeStartDay(String challengeStartDay) {
        this.challengeStartDay = challengeStartDay;
    }

    public String getChallengeEndDay() {
        return challengeEndDay;
    }

    public void setChallengeEndDay(String challengeEndDay) {
        this.challengeEndDay = challengeEndDay;
    }

    public String getChallengeDescription() {
        return challengeDescription;
    }

    public void setChallengeDescription(String challengeDescription) {
        this.challengeDescription = challengeDescription;
    }

    public int getChallengeDay() {
        return challengeDay;
    }

    public void setChallengeDay(int challengeDay) {
        this.challengeDay = challengeDay;
    }

    public List<Boolean> getChallangeStatus() {
        return challangeStatus;
    }

    public void setChallangeStatus(List<Boolean> challangeStatus) {
        this.challangeStatus = challangeStatus;
    }
}
