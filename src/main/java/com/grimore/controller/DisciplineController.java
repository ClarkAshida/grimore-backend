package com.grimore.controller;

import com.grimore.dto.request.CreateDisciplineDTO;
import com.grimore.dto.response.DisciplineDTO;
import com.grimore.service.DisciplineService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/disciplines")
@RequiredArgsConstructor
public class DisciplineController {

    private final DisciplineService disciplineService;

    @PostMapping
    public ResponseEntity<@NonNull DisciplineDTO> create(@Valid @RequestBody CreateDisciplineDTO dto) {
        DisciplineDTO created = disciplineService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<@NonNull DisciplineDTO> findById(@PathVariable Integer id) {
        DisciplineDTO discipline = disciplineService.findCurrentStudentDisciplineById(id);
        return ResponseEntity.ok(discipline);
    }

    @GetMapping
    public ResponseEntity<List<@NonNull DisciplineDTO>> findAll(
            @RequestParam(defaultValue = "true") boolean activeOnly) {
        List<DisciplineDTO> disciplines = disciplineService.findCurrentStudentDisciplines(activeOnly);
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