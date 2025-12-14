package com.grimore.exception.validation;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException() {
        super("Requisição inválida");
    }
}

