package com.grimore.dto.response;

public record LoginResponseDTO(
    String token,
    String refreshToken,
    String fullName,
    String email,
    Long expiresIn
) {
}