package com.grimore.dto.response;

public record TokenResponseDTO(
    String accessToken,
    String refreshToken,
    String type,
    Long expiresIn
) {
    public TokenResponseDTO(String accessToken, String refreshToken, Long expiresIn) {
        this(accessToken, refreshToken, "Bearer", expiresIn);
    }
}