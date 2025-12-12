package com.grimore.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.grimore.enums.TaskType;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TaskDTO(
    UUID id,
    UUID disciplineId,
    String disciplineName,
    String title,
    TaskType type,
    LocalDateTime dueDate,
    Boolean completed,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}