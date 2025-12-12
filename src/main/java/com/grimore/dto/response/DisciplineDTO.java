package com.grimore.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.grimore.enums.WorkloadHours;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DisciplineDTO(
    Integer id,
    Integer studentId,
    String name,
    String code,
    String scheduleCode,
    String location,
    String colorHex,
    WorkloadHours workloadHours,
    Integer absencesHours,
    Boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}