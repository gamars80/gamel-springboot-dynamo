package com.example.gamel.exceptions;

public class UnableToAcquireLockException extends RuntimeException {
    public UnableToAcquireLockException(String message) {
        super(message);
    }
}