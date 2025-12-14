package com.grimore.exception.auth;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException() {
        super("Você não tem permissão para acessar este recurso");
    }
}

