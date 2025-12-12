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
        DisciplineDTO discipline = disciplineService.findById(id);
        return ResponseEntity.ok(discipline);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<@NonNull DisciplineDTO>> findByStudentId(
            @PathVariable Integer studentId,
            @RequestParam(defaultValue = "true") boolean activeOnly) {
        List<DisciplineDTO> disciplines = disciplineService.findByStudentId(studentId, activeOnly);
        return ResponseEntity.ok(disciplines);
    }

    @PutMapping("/{id}")
    public ResponseEntity<@NonNull DisciplineDTO> update(
            @PathVariable Integer id,
            @Valid @RequestBody CreateDisciplineDTO dto) {
        DisciplineDTO updated = disciplineService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Integer id) {
        disciplineService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        disciplineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}