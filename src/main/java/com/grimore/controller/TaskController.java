package com.grimore.controller;

import com.grimore.dto.request.CreateTaskDTO;
import com.grimore.dto.response.TaskDTO;
import com.grimore.service.TaskService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<@NonNull TaskDTO> create(@Valid @RequestBody CreateTaskDTO dto) {
        TaskDTO created = taskService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<@NonNull TaskDTO> findById(@PathVariable Integer id) {
        TaskDTO task = taskService.findById(id);
        return ResponseEntity.ok(task);
    }

    @GetMapping
    public ResponseEntity<List<@NonNull TaskDTO>> findAll() {
        List<TaskDTO> tasks = taskService.findAll();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/discipline/{disciplineId}")
    public ResponseEntity<List<@NonNull TaskDTO>> findByDisciplineId(
            @PathVariable Integer disciplineId,
            @RequestParam(required = false) Boolean completed) {
        List<TaskDTO> tasks = taskService.findByDisciplineId(disciplineId, completed);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<@NonNull TaskDTO>> findByStudentId(
            @PathVariable Integer studentId,
            @RequestParam(required = false) Boolean completed) {
        List<TaskDTO> tasks = taskService.findByStudentId(studentId, completed);
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{id}")
    public ResponseEntity<@NonNull TaskDTO> update(
            @PathVariable Integer id,
            @Valid @RequestBody CreateTaskDTO dto) {
        TaskDTO updated = taskService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/toggle-completed")
    public ResponseEntity<@NonNull TaskDTO> toggleCompleted(@PathVariable Integer id) {
        TaskDTO updated = taskService.toggleCompleted(id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
