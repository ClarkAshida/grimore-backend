package com.grimore.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record StudentDTO(
    UUID id,
    String fullName,
    String email,
    Boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}