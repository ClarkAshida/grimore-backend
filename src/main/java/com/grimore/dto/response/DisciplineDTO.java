package com.grimore.dto.response;

import com.grimore.enums.DisciplineNature;
import com.grimore.enums.DisciplineStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.grimore.enums.TotalHours;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DisciplineDTO(
    UUID id,
    UUID studentId,
    String name,
    String code,
    String location,
    DisciplineNature nature,
    Integer semester,
    DisciplineStatus status,
    TotalHours totalHours,
    Integer absencesCount,
    String classSchedules
) {}