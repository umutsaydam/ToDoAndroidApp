package com.example.todoapp.Models;


public class ToDoModel {
    String id;
    String content;
    boolean selected;
    String  date;

    public ToDoModel() {
    }

    public ToDoModel(String id, String content, boolean selected, String date) {
        this.id = id;
        this.content = content;
        this.selected = selected;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
