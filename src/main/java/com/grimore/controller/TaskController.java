package com.grimore.controller;

import com.grimore.dto.request.CreateTaskDTO;
import com.grimore.dto.response.TaskDTO;
import com.grimore.dto.response.TaskSummaryDTO;
import com.grimore.service.TaskService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        TaskDTO task = taskService.findCurrentStudentTaskById(id);
        return ResponseEntity.ok(task);
    }

    @GetMapping
    public ResponseEntity<Page<@NonNull TaskSummaryDTO>> findAll(
            @RequestParam(required = false) Boolean completed,
            Pageable pageable) {
        Page<TaskSummaryDTO> tasks = taskService.findCurrentStudentTasks(completed, pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/discipline/{disciplineId}")
    public ResponseEntity<Page<@NonNull TaskSummaryDTO>> findByDiscipline(
            @PathVariable Integer disciplineId,
            @RequestParam(required = false) Boolean completed,
            Pageable pageable) {
        Page<TaskSummaryDTO> tasks = taskService.findCurrentStudentTasksByDiscipline(disciplineId, completed, pageable);
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{id}")
    public ResponseEntity<@NonNull TaskDTO> update(
            @PathVariable Integer id,
            @Valid @RequestBody CreateTaskDTO dto) {
        TaskDTO updated = taskService.updateCurrentStudentTask(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/toggle-completed")
    public ResponseEntity<@NonNull TaskDTO> toggleCompleted(@PathVariable Integer id) {
        TaskDTO updated = taskService.toggleCurrentStudentTaskCompleted(id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        taskService.deleteCurrentStudentTask(id);
        return ResponseEntity.noContent().build();
    }
}