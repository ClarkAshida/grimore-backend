package com.grimore.dto.response;

public record LoginResponseDTO(
    String token,
    String fullName,
    String email
) {
}