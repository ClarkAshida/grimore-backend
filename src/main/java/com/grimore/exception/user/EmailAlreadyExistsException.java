package com.grimore.exception.user;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super(String.format("Email '%s' já está registrado", email));
    }

    public EmailAlreadyExistsException() {
        super("Este email já está registrado");
    }
}

