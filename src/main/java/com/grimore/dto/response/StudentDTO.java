package com.grimore.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record StudentDTO(
    Integer id,
    String fullName,
    String email,
    Boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}