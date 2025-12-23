package com.grimore.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.grimore.enums.WorkloadHours;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DisciplineSummaryDTO(
    Integer id,
    String name,
    String code,
    String colorHex,
    WorkloadHours workloadHours,
    Integer absencesHours,
    Boolean active
) {}

