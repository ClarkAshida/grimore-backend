package com.grimore.exception.user;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String username) {
        super(String.format("Username '%s' is already taken", username));
    }

    public UsernameAlreadyExistsException() {
        super("Username is already taken");
    }
}

