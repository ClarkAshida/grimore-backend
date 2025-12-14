package com.grimore.exception.validation;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(String message) {
        super(message);
    }

    public InvalidPasswordException() {
        super("Essa senha não atende aos requisitos de segurança");
    }
}