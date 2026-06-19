package com.springboot.taskflow.taskflow.exceptions;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message) {
        super(message);
    }

}