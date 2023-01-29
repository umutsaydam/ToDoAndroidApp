package com.example.todoapp.Models;



public class ToDoLayerModel {
   private final String timePeriod;

    public ToDoLayerModel(String timePeriod) {
        this.timePeriod = timePeriod;
    }

    public String getTimePeriod() {
        return timePeriod;
    }
}
