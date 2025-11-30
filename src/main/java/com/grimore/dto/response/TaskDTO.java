package com.grimore.dto.response;

import com.grimore.enums.TaskPriority;
import com.grimore.enums.TaskStatus;
import com.grimore.enums.TaskType;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TaskDTO(
    UUID id,
    UUID disciplineId,
    String disciplineName,
    String title,
    String description,
    TaskType taskType,
    TaskStatus status,
    TaskPriority priority,
    LocalDateTime dueDate,
    Double gradeWeight,
    Double gradeObtained
) {}