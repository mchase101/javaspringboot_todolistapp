package com.example.todoapp.exception;

public class MessageNotReadableException extends RuntimeException{
        public MessageNotReadableException(){
        super("Invalid Data. Please try again.");
    }

}
