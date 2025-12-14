package com.grimore.controller;

import com.grimore.dto.request.CreateStudentDTO;
import com.grimore.dto.response.MessageResponseDTO;
import com.grimore.dto.response.StudentDTO;
import com.grimore.service.StudentService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping("/register")
    public ResponseEntity<@NonNull StudentDTO> register(@Valid @RequestBody CreateStudentDTO dto) {
        StudentDTO created = studentService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/profile")
    public ResponseEntity<@NonNull StudentDTO> getProfile() {
        StudentDTO profile = studentService.getCurrentProfile();
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    public ResponseEntity<@NonNull StudentDTO> updateProfile(@Valid @RequestBody CreateStudentDTO dto) {
        StudentDTO updated = studentService.updateCurrentProfile(dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/profile")
    public ResponseEntity<MessageResponseDTO> deactivateProfile() {
        studentService.deactivateCurrentProfile();
        return ResponseEntity.ok(new MessageResponseDTO("Conta desativada com sucesso"));
    }
}