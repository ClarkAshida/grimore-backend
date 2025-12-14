package com.grimore.exception.resource;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }

    public ConflictException() {
        super("Conflito de recurso");
    }
}

