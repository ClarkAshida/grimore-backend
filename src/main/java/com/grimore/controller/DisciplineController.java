package com.grimore.controller;

import com.grimore.dto.request.CreateDisciplineDTO;
import com.grimore.dto.response.DisciplineDTO;
import com.grimore.dto.response.DisciplineSummaryDTO;
import com.grimore.dto.response.ImportDisciplinesResultDTO;
import com.grimore.service.DisciplinePdfImportService;
import com.grimore.service.DisciplineService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/disciplines")
@RequiredArgsConstructor
public class DisciplineController {

    private final DisciplineService disciplineService;
    private final DisciplinePdfImportService disciplinePdfImportService;

    @PostMapping
    public ResponseEntity<@NonNull DisciplineDTO> create(@Valid @RequestBody CreateDisciplineDTO dto) {
        DisciplineDTO created = disciplineService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping(value = "/import/enrollment-pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportDisciplinesResultDTO> importFromEnrollmentPdf(
            @RequestPart("file") MultipartFile file
    ) {
        ImportDisciplinesResultDTO result = disciplinePdfImportService.importEnrollmentPdf(file);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<@NonNull DisciplineDTO> findById(@PathVariable Integer id) {
        DisciplineDTO discipline = disciplineService.findCurrentStudentDisciplineById(id);
        return ResponseEntity.ok(discipline);
    }

    @GetMapping()
    public ResponseEntity<Page<@NonNull DisciplineSummaryDTO>> findAll(
            @RequestParam(defaultValue = "true") boolean activeOnly,
            Pageable pageable) {
        Page<DisciplineSummaryDTO> disciplines = disciplineService.findCurrentStudentDisciplines(activeOnly, pageable);
        return ResponseEntity.ok(disciplines);
    }

    @PutMapping("/{id}")
    public ResponseEntity<@NonNull DisciplineDTO> update(
            @PathVariable Integer id,
            @Valid @RequestBody CreateDisciplineDTO dto) {
        DisciplineDTO updated = disciplineService.updateCurrentStudentDiscipline(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Integer id) {
        disciplineService.deactivateCurrentStudentDiscipline(id);
        return ResponseEntity.noContent().build();
    }
}