package com.grimore.dto.response;

import com.grimore.dto.response.DisciplineDTO;
import java.util.List;

public record BatchCreateReportDTO(
        List<DisciplineDTO> created,
        List<String> errors
) {}
