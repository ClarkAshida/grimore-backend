package com.grimore.controller;

import com.grimore.dto.request.CreateStudentDTO;
import com.grimore.dto.response.StudentDTO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    @PostMapping
    @Transactional
    public void createStudent(@RequestBody CreateStudentDTO data) {
        // Implementation for creating a student
    }

    @GetMapping
    public List<StudentDTO> getAllStudents() {
        // Implementation for retrieving all students
        return null;
    }

    @GetMapping("/{id}")
    public StudentDTO getStudentById(@PathVariable String id) {
        // Implementation for retrieving a student by ID
        return null;
    }

    @PatchMapping
    @Transactional
    public void updateStudent(@RequestBody StudentDTO data) {
        // Implementation for updating a student
    }

    @DeleteMapping
    @Transactional
    public void deleteStudent(@RequestParam String id) {
        // Implementation for deleting a student by ID
    }
}
