package com.pr4;

public class InvalidBookDataException extends IllegalArgumentException {
    public InvalidBookDataException(String message) {
        super(message);
    }
}