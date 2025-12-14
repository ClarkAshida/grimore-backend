package com.grimore.exception.user;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super(String.format("Email '%s' is already registered", email));
    }

    public EmailAlreadyExistsException() {
        super("Email is already registered");
    }
}

