package com.grimore.exception.server;

public class InternalServerErrorException extends RuntimeException {
    public InternalServerErrorException(String message) {
        super(message);
    }

    public InternalServerErrorException() {
        super("Erro interno do servidor");
    }

    public InternalServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}

