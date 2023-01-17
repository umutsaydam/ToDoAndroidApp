package com.example.todoapp.Models;

public class ToDoModel {
    String id;
    String content;
    boolean selected;

    public ToDoModel() {
    }

    public ToDoModel(String id, String content, boolean selected) {
        this.id = id;
        this.content = content;
        this.selected = selected;
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
}
