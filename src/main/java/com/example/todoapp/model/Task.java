package com.example.todoapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Task {
    @Id                                                     // Primary Key
    @GeneratedValue(strategy = GenerationType.IDENTITY)     // Auto-incrementing of the primary key value
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Task cannot be empty. Please enter a task.")
    @Size(max = 50, message = "Task must not exceed 50 characters.")
    private String task;

    // Set to false so it will default to incomplete if user does not provide a value
    private boolean completed = false;

    public Task() {
    }

    public Task(String task, boolean completed) {
        this.task = task;
        this.completed = completed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}





