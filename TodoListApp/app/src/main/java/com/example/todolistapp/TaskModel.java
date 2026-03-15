package com.example.todolistapp;

public class TaskModel {

    public int id;
    public String title;
    public String description;
    public String priority;
    public String dueDate;
    public int status;

    public TaskModel(int id, String title, String description,
                     String priority, String dueDate, int status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.status = status;
    }
}