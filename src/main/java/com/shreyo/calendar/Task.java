package com.shreyo.calendar;

import java.time.LocalDate;

public class Task {
    private String title;
    private String content;
    private LayoutHandler.TaskPriority priority;
    private LocalDate date;

    public Task(String title, String content, LayoutHandler.TaskPriority priority, LocalDate date) {
        this.title = title;
        this.content = content;
        this.priority = priority;
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public LayoutHandler.TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(LayoutHandler.TaskPriority priority) {
        this.priority = priority;
    }
}
