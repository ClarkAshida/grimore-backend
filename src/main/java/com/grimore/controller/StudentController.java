package com.grimore.controller;

import com.grimore.dto.request.CreateStudentDTO;
import com.grimore.dto.response.StudentDTO;
import com.grimore.service.StudentService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<@NonNull StudentDTO> create(@Valid @RequestBody CreateStudentDTO dto) {
        StudentDTO created = studentService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<@NonNull StudentDTO> findById(@PathVariable Integer id) {
        StudentDTO student = studentService.findById(id);
        return ResponseEntity.ok(student);
    }

    @GetMapping
    public ResponseEntity<List<@NonNull StudentDTO>> findAll(
            @RequestParam(defaultValue = "true") boolean activeOnly) {
        List<StudentDTO> students = studentService.findAll(activeOnly);
        return ResponseEntity.ok(students);
    }

    @PutMapping("/{id}")
    public ResponseEntity<@NonNull StudentDTO> update(
            @PathVariable Integer id,
            @Valid @RequestBody CreateStudentDTO dto) {
        StudentDTO updated = studentService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        studentService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
