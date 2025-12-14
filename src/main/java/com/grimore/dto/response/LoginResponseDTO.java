package com.grimore.dto.response;

import com.grimore.enums.Role;

public record LoginResponseDTO(
    String token,
    String refreshToken,
    String fullName,
    String email,
    Role role,
    Long expiresIn
) {
}