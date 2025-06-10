package com.example.taskflow.exceptions;

public class UnauthorizedException extends RuntimeException {

    //Constructor with error message.

    public UnauthorizedException(String message) {
        super(message);
    }

  //Constructor with error message and cause.

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
