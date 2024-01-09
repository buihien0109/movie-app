package com.example.movieapp.exception;

public class ResouceNotFoundException extends RuntimeException {
    public ResouceNotFoundException(String message) {
        super(message);
    }
}
