package com.grimore.exception.resource;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resource, String field, Object value) {
        super(String.format("%s não encontrado(a) com o campo %s igual a '%s'", resource, field, value));
    }
}


