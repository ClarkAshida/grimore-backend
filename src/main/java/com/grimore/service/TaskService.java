package com.grimore.service;

import com.grimore.dto.request.CreateTaskDTO;
import com.grimore.dto.response.TaskDTO;
import com.grimore.mapper.TaskMapper;
import com.grimore.model.Discipline;
import com.grimore.model.Task;
import com.grimore.repository.DisciplineRepository;
import com.grimore.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final DisciplineRepository disciplineRepository;
    private final TaskMapper mapper;

    @Transactional
    public TaskDTO create(CreateTaskDTO dto) {
        Discipline discipline = findDisciplineById(dto.disciplineId());

        Task task = mapper.toEntity(dto);
        task.setDiscipline(discipline);
        task.setCompleted(false);

        return mapper.toDTO(taskRepository.save(task));
    }

    @Transactional(readOnly = true)
    public TaskDTO findById(Integer id) {
        Task task = findTaskById(id);
        return mapper.toDTO(task);
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> findByDisciplineId(Integer disciplineId, Boolean completed) {
        if (!disciplineRepository.existsById(disciplineId)) {
            throw new EntityNotFoundException("Disciplina não encontrada");
        }

        List<Task> tasks = completed != null
                ? taskRepository.findByDisciplineIdAndCompleted(disciplineId, completed)
                : taskRepository.findByDisciplineId(disciplineId);

        return mapper.toDTO(tasks);
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> findByStudentId(Integer studentId, Boolean completed) {
        List<Task> tasks = completed != null
                ? taskRepository.findByDiscipline_StudentIdAndCompleted(studentId, completed)
                : taskRepository.findByDiscipline_StudentId(studentId);

        return mapper.toDTO(tasks);
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> findAll() {
        List<Task> tasks = taskRepository.findAll();
        return mapper.toDTO(tasks);
    }

    @Transactional
    public TaskDTO update(Integer id, CreateTaskDTO dto) {
        Task task = findTaskById(id);
        Discipline discipline = findDisciplineById(dto.disciplineId());

        mapper.updateEntity(dto, task);
        task.setDiscipline(discipline);

        return mapper.toDTO(taskRepository.save(task));
    }

    @Transactional
    public TaskDTO toggleCompleted(Integer id) {
        Task task = findTaskById(id);
        task.setCompleted(!task.getCompleted());
        return mapper.toDTO(taskRepository.save(task));
    }

    @Transactional
    public void delete(Integer id) {
        if (!taskRepository.existsById(id)) {
            throw new EntityNotFoundException("Tarefa não encontrada");
        }
        taskRepository.deleteById(id);
    }

    private Task findTaskById(Integer id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada"));
    }

    private Discipline findDisciplineById(Integer id) {
        return disciplineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Disciplina não encontrada"));
    }
}
