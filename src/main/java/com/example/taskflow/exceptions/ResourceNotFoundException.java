package com.example.taskflow.exceptions;


public class ResourceNotFoundException extends RuntimeException {

    //Constructor with error message.

    public ResourceNotFoundException(String message) {
        super(message);
    }


    //Constructor with error message and cause.

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
