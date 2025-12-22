package com.grimore.dto.response;

import com.grimore.dto.request.ExtractedDisciplineDTO;

import java.util.List;

public class ExtractedDisciplinesResponse {
    private List<ExtractedDisciplineDTO> disciplines;

    public List<ExtractedDisciplineDTO> getDisciplines() {
        return disciplines;
    }

    public void setDisciplines(List<ExtractedDisciplineDTO> disciplines) {
        this.disciplines = disciplines;
    }
}
