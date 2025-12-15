package com.grimore.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.grimore.enums.TaskType;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TaskSummaryDTO(
    Integer id,
    Integer disciplineId,
    String disciplineName,
    String title,
    TaskType type,
    LocalDateTime dueDate,
    Boolean completed
) {}

