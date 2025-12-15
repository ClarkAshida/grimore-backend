package com.grimore.service;

import com.grimore.dto.request.CreateTaskDTO;
import com.grimore.dto.response.TaskDTO;
import com.grimore.exception.resource.ResourceNotFoundException;
import com.grimore.exception.validation.BadRequestException;
import com.grimore.mapper.TaskMapper;
import com.grimore.model.Discipline;
import com.grimore.model.Task;
import com.grimore.repository.DisciplineRepository;
import com.grimore.repository.TaskRepository;
import com.grimore.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final DisciplineRepository disciplineRepository;
    private final TaskMapper mapper;

    @Transactional
    public TaskDTO create(CreateTaskDTO dto) {
        validateCreateDTO(dto);
        Integer currentStudentId = SecurityUtils.getCurrentStudentId();
        Discipline discipline = findDisciplineById(dto.disciplineId());

        if (!discipline.getStudent().getId().equals(currentStudentId)) {
            throw new BadRequestException("Você não tem acesso a esta disciplina");
        }

        if (!discipline.getActive()) {
            throw new BadRequestException("Não é possível criar tarefa para disciplina inativa");
        }

        try {
            Task task = mapper.toEntity(dto);
            task.setDiscipline(discipline);
            task.setCompleted(false);

            Task saved = taskRepository.save(task);
            log.info("Task created successfully for discipline: {}", dto.disciplineId());

            return mapper.toDTO(saved);
        } catch (Exception ex) {
            log.error("Error creating task", ex);
            throw new BadRequestException("Falha ao criar tarefa");
        }
    }

    @Transactional(readOnly = true)
    public TaskDTO findCurrentStudentTaskById(Integer id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("ID de tarefa inválido");
        }

        Integer currentStudentId = SecurityUtils.getCurrentStudentId();
        Task task = findTaskById(id);

        if (!task.getDiscipline().getStudent().getId().equals(currentStudentId)) {
            throw new BadRequestException("Você não tem acesso a esta tarefa");
        }

        return mapper.toDTO(task);
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> findCurrentStudentTasks(Boolean completed) {
        try {
            Integer currentStudentId = SecurityUtils.getCurrentStudentId();

            List<Task> tasks = completed != null
                    ? taskRepository.findByDiscipline_StudentIdAndCompleted(currentStudentId, completed)
                    : taskRepository.findByDiscipline_StudentId(currentStudentId);

            log.info("Retrieved {} tasks for current student", tasks.size());
            return mapper.toDTO(tasks);
        } catch (Exception ex) {
            log.error("Error fetching current student tasks", ex);
            throw new BadRequestException("Falha ao buscar tarefas");
        }
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> findCurrentStudentTasksByDiscipline(Integer disciplineId, Boolean completed) {
        if (disciplineId == null || disciplineId <= 0) {
            throw new BadRequestException("ID de disciplina inválido");
        }

        try {
            Integer currentStudentId = SecurityUtils.getCurrentStudentId();
            Discipline discipline = findDisciplineById(disciplineId);

            if (!discipline.getStudent().getId().equals(currentStudentId)) {
                throw new BadRequestException("Você não tem acesso a esta disciplina");
            }

            if (!discipline.getActive()) {
                throw new BadRequestException("Esta disciplina foi desativada");
            }

            List<Task> tasks = completed != null
                    ? taskRepository.findByDisciplineIdAndCompleted(disciplineId, completed)
                    : taskRepository.findByDisciplineId(disciplineId);

            log.info("Retrieved {} tasks for discipline: {}", tasks.size(), disciplineId);
            return mapper.toDTO(tasks);
        } catch (Exception ex) {
            log.error("Error fetching tasks for discipline: {}", disciplineId, ex);
            throw new BadRequestException("Falha ao buscar tarefas");
        }
    }


    @Transactional
    public TaskDTO updateCurrentStudentTask(Integer id, CreateTaskDTO dto) {
        if (id == null || id <= 0) {
            throw new BadRequestException("ID de tarefa inválido");
        }

        validateCreateDTO(dto);
        Integer currentStudentId = SecurityUtils.getCurrentStudentId();
        Task task = findTaskById(id);

        if (!task.getDiscipline().getStudent().getId().equals(currentStudentId)) {
            throw new BadRequestException("Você não tem acesso a esta tarefa");
        }

        Discipline discipline = findDisciplineById(dto.disciplineId());

        if (!discipline.getStudent().getId().equals(currentStudentId)) {
            throw new BadRequestException("Você não tem acesso a esta disciplina");
        }

        try {
            mapper.updateEntity(dto, task);
            task.setDiscipline(discipline);

            Task updated = taskRepository.save(task);
            log.info("Task updated successfully: {}", id);

            return mapper.toDTO(updated);
        } catch (Exception ex) {
            log.error("Error updating task: {}", id, ex);
            throw new BadRequestException("Falha ao atualizar tarefa");
        }
    }

    @Transactional
    public TaskDTO toggleCurrentStudentTaskCompleted(Integer id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("ID de tarefa inválido");
        }

        try {
            Integer currentStudentId = SecurityUtils.getCurrentStudentId();
            Task task = findTaskById(id);

            if (!task.getDiscipline().getStudent().getId().equals(currentStudentId)) {
                throw new BadRequestException("Você não tem acesso a esta tarefa");
            }

            task.setCompleted(!task.getCompleted());

            Task updated = taskRepository.save(task);
            log.info("Task completion toggled: {} - Completed: {}", id, updated.getCompleted());

            return mapper.toDTO(updated);
        } catch (ResourceNotFoundException | BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error toggling task completion: {}", id, ex);
            throw new BadRequestException("Falha ao atualizar tarefa");
        }
    }

    @Transactional
    public void deleteCurrentStudentTask(Integer id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("ID de tarefa inválido");
        }

        try {
            Integer currentStudentId = SecurityUtils.getCurrentStudentId();
            Task task = findTaskById(id);

            if (!task.getDiscipline().getStudent().getId().equals(currentStudentId)) {
                throw new BadRequestException("Você não tem acesso a esta tarefa");
            }

            taskRepository.deleteById(id);
            log.info("Task deleted successfully: {}", id);
        } catch (ResourceNotFoundException | BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error deleting task: {}", id, ex);
            throw new BadRequestException("Falha ao deletar tarefa");
        }
    }

    // Métodos para Admin
    @Transactional(readOnly = true)
    public TaskDTO findById(Integer id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("ID de tarefa inválido");
        }

        Task task = findTaskById(id);
        return mapper.toDTO(task);
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> findByDisciplineId(Integer disciplineId, Boolean completed) {
        if (disciplineId == null || disciplineId <= 0) {
            throw new BadRequestException("ID de disciplina inválido");
        }

        if (!disciplineRepository.existsById(disciplineId)) {
            throw new ResourceNotFoundException("Disciplina", "id", disciplineId);
        }

        try {
            List<Task> tasks = completed != null
                    ? taskRepository.findByDisciplineIdAndCompleted(disciplineId, completed)
                    : taskRepository.findByDisciplineId(disciplineId);

            return mapper.toDTO(tasks);
        } catch (Exception ex) {
            log.error("Error fetching tasks for discipline: {}", disciplineId, ex);
            throw new BadRequestException("Falha ao buscar tarefas");
        }
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> findByStudentId(Integer studentId, Boolean completed) {
        if (studentId == null || studentId <= 0) {
            throw new BadRequestException("ID de estudante inválido");
        }

        try {
            List<Task> tasks = completed != null
                    ? taskRepository.findByDiscipline_StudentIdAndCompleted(studentId, completed)
                    : taskRepository.findByDiscipline_StudentId(studentId);

            return mapper.toDTO(tasks);
        } catch (Exception ex) {
            log.error("Error fetching tasks for student: {}", studentId, ex);
            throw new BadRequestException("Falha ao buscar tarefas");
        }
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> findAll() {
        try {
            List<Task> tasks = taskRepository.findAll();
            return mapper.toDTO(tasks);
        } catch (Exception ex) {
            log.error("Error fetching all tasks", ex);
            throw new BadRequestException("Falha ao buscar tarefas");
        }
    }

    @Transactional
    public void delete(Integer id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("ID de tarefa inválido");
        }

        try {
            if (!taskRepository.existsById(id)) {
                throw new ResourceNotFoundException("Tarefa", "id", id);
            }

            taskRepository.deleteById(id);
            log.info("Task deleted successfully: {}", id);
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error deleting task: {}", id, ex);
            throw new BadRequestException("Falha ao deletar tarefa");
        }
    }

    private Task findTaskById(Integer id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa", "id", id));
    }

    private Discipline findDisciplineById(Integer id) {
        return disciplineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina", "id", id));
    }

    private void validateCreateDTO(CreateTaskDTO dto) {
        if (dto == null) {
            throw new BadRequestException("Dados da tarefa são obrigatórios");
        }
        if (dto.disciplineId() == null || dto.disciplineId() <= 0) {
            throw new BadRequestException("ID de disciplina válido é obrigatório");
        }
        if (dto.title() == null || dto.title().isBlank()) {
            throw new BadRequestException("Título da tarefa é obrigatório");
        }
    }
}