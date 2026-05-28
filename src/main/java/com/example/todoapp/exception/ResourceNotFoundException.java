package com.example.todoapp.exception;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(String message){
        super(String.format("%s Please try again. ", message));
    }

    public ResourceNotFoundException() {
        super("Resource not found.");

    }
}
