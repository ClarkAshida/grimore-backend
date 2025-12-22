package com.grimore.dto.response;

import java.util.List;

public record ImportDisciplinesResultDTO(
        int extractedCount,
        int createdCount,
        List<DisciplineDTO> created,
        List<String> errors
) {}
