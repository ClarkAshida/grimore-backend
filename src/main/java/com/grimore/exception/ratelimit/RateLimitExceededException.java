package com.grimore.exception.ratelimit;

public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException(String message) {
        super(message);
    }

    public RateLimitExceededException() {
        super("Limite de uso excedido. Por favor tente novamente mais tarde.");
    }
}

