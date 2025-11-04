package com.example.exception;

public class BorrowerNotFoundException extends RuntimeException {

    public BorrowerNotFoundException(String message) {
        super(message);
    }

}
