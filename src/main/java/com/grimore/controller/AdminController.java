package com.grimore.controller;

import com.grimore.dto.response.DisciplineDTO;
import com.grimore.dto.response.StudentDTO;
import com.grimore.dto.response.TaskDTO;
import com.grimore.service.DisciplineService;
import com.grimore.service.StudentService;
import com.grimore.service.TaskService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final StudentService studentService;
    private final DisciplineService disciplineService;
    private final TaskService taskService;

    // ===== STUDENT ENDPOINTS =====

    @GetMapping("/students")
    public ResponseEntity<Page<@NonNull StudentDTO>> getAllStudents(
            @RequestParam(defaultValue = "true") boolean activeOnly,
            Pageable pageable) {
        Page<StudentDTO> students = studentService.findAll(activeOnly, pageable);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<@NonNull StudentDTO> getStudentById(@PathVariable Integer id) {
        StudentDTO student = studentService.findById(id);
        return ResponseEntity.ok(student);
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<Void> deactivateStudent(@PathVariable Integer id) {
        studentService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/students/{id}/hard-delete")
    public ResponseEntity<Void> deleteStudent(@PathVariable Integer id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ===== DISCIPLINE ENDPOINTS =====

    @GetMapping("/disciplines")
    public ResponseEntity<List<@NonNull DisciplineDTO>> getAllDisciplines() {
        // Você pode criar um método findAll() no DisciplineService
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/disciplines/{id}")
    public ResponseEntity<@NonNull DisciplineDTO> getDisciplineById(@PathVariable Integer id) {
        DisciplineDTO discipline = disciplineService.findById(id);
        return ResponseEntity.ok(discipline);
    }

    @GetMapping("/students/{studentId}/disciplines")
    public ResponseEntity<List<@NonNull DisciplineDTO>> getStudentDisciplines(
            @PathVariable Integer studentId,
            @RequestParam(defaultValue = "true") boolean activeOnly) {
        List<DisciplineDTO> disciplines = disciplineService.findByStudentId(studentId, activeOnly);
        return ResponseEntity.ok(disciplines);
    }

    @DeleteMapping("/disciplines/{id}")
    public ResponseEntity<Void> deactivateDiscipline(@PathVariable Integer id) {
        disciplineService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    // ===== TASK ENDPOINTS =====

    @GetMapping("/tasks")
    public ResponseEntity<Page<@NonNull TaskDTO>> getAllTasks(
            @RequestParam(required = false) Boolean completed,
            Pageable pageable) {
        Page<TaskDTO> tasks = taskService.findAll(completed, pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<@NonNull TaskDTO> getTaskById(@PathVariable Integer id) {
        TaskDTO task = taskService.findById(id);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/students/{studentId}/tasks")
    public ResponseEntity<Page<@NonNull TaskDTO>> getStudentTasks(
            @PathVariable Integer studentId,
            @RequestParam(required = false) Boolean completed,
            Pageable pageable) {
        Page<TaskDTO> tasks = taskService.findByStudentId(studentId, completed, pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/disciplines/{disciplineId}/tasks")
    public ResponseEntity<Page<@NonNull TaskDTO>> getDisciplineTasks(
            @PathVariable Integer disciplineId,
            @RequestParam(required = false) Boolean completed,
            Pageable pageable) {
        Page<TaskDTO> tasks = taskService.findByDisciplineId(disciplineId, completed, pageable);
        return ResponseEntity.ok(tasks);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Integer id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}