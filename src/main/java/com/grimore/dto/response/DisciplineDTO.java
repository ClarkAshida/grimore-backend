package com.grimore.dto.response;

import com.grimore.enums.DisciplineStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DisciplineDTO(
    UUID id,
    UUID studentId,
    String name,
    String code,
    String location,
    Integer semester,
    DisciplineStatus status,
    Integer totalHours,
    Integer absencesCount,
    String classSchedules
) {}