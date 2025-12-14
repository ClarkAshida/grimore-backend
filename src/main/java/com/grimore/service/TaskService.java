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
            Discipline discipline = findDisciplineById(dto.disciplineId());

            if (!discipline.getActive()) {
                throw new BadRequestException("Cannot create task for inactive discipline");
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
                throw new BadRequestException("Failed to create task");
            }
        }

        @Transactional(readOnly = true)
        public TaskDTO findById(Integer id) {
            if (id == null || id <= 0) {
                throw new BadRequestException("Invalid task ID");
            }

            Task task = findTaskById(id);
            return mapper.toDTO(task);
        }

        @Transactional(readOnly = true)
        public List<TaskDTO> findByDisciplineId(Integer disciplineId, Boolean completed) {
            if (disciplineId == null || disciplineId <= 0) {
                throw new BadRequestException("Invalid discipline ID");
            }

            if (!disciplineRepository.existsById(disciplineId)) {
                throw new ResourceNotFoundException("Discipline", "id", disciplineId);
            }

            try {
                List<Task> tasks = completed != null
                        ? taskRepository.findByDisciplineIdAndCompleted(disciplineId, completed)
                        : taskRepository.findByDisciplineId(disciplineId);

                return mapper.toDTO(tasks);
            } catch (Exception ex) {
                log.error("Error fetching tasks for discipline: {}", disciplineId, ex);
                throw new BadRequestException("Failed to fetch tasks");
            }
        }

        @Transactional(readOnly = true)
        public List<TaskDTO> findByStudentId(Integer studentId, Boolean completed) {
            if (studentId == null || studentId <= 0) {
                throw new BadRequestException("Invalid student ID");
            }

            try {
                List<Task> tasks = completed != null
                        ? taskRepository.findByDiscipline_StudentIdAndCompleted(studentId, completed)
                        : taskRepository.findByDiscipline_StudentId(studentId);

                return mapper.toDTO(tasks);
            } catch (Exception ex) {
                log.error("Error fetching tasks for student: {}", studentId, ex);
                throw new BadRequestException("Failed to fetch tasks");
            }
        }

        @Transactional(readOnly = true)
        public List<TaskDTO> findAll() {
            try {
                List<Task> tasks = taskRepository.findAll();
                return mapper.toDTO(tasks);
            } catch (Exception ex) {
                log.error("Error fetching all tasks", ex);
                throw new BadRequestException("Failed to fetch tasks");
            }
        }

        @Transactional
        public TaskDTO update(Integer id, CreateTaskDTO dto) {
            if (id == null || id <= 0) {
                throw new BadRequestException("Invalid task ID");
            }

            validateCreateDTO(dto);
            Task task = findTaskById(id);
            Discipline discipline = findDisciplineById(dto.disciplineId());

            try {
                mapper.updateEntity(dto, task);
                task.setDiscipline(discipline);

                Task updated = taskRepository.save(task);
                log.info("Task updated successfully: {}", id);

                return mapper.toDTO(updated);
            } catch (Exception ex) {
                log.error("Error updating task: {}", id, ex);
                throw new BadRequestException("Failed to update task");
            }
        }

        @Transactional
        public TaskDTO toggleCompleted(Integer id) {
            if (id == null || id <= 0) {
                throw new BadRequestException("Invalid task ID");
            }

            try {
                Task task = findTaskById(id);
                task.setCompleted(!task.getCompleted());

                Task updated = taskRepository.save(task);
                log.info("Task completion toggled: {} - Completed: {}", id, updated.getCompleted());

                return mapper.toDTO(updated);
            } catch (ResourceNotFoundException ex) {
                throw ex;
            } catch (Exception ex) {
                log.error("Error toggling task completion: {}", id, ex);
                throw new BadRequestException("Failed to update task");
            }
        }

        @Transactional
        public void delete(Integer id) {
            if (id == null || id <= 0) {
                throw new BadRequestException("Invalid task ID");
            }

            try {
                if (!taskRepository.existsById(id)) {
                    throw new ResourceNotFoundException("Task", "id", id);
                }

                taskRepository.deleteById(id);
                log.info("Task deleted successfully: {}", id);
            } catch (ResourceNotFoundException ex) {
                throw ex;
            } catch (Exception ex) {
                log.error("Error deleting task: {}", id, ex);
                throw new BadRequestException("Failed to delete task");
            }
        }

        private Task findTaskById(Integer id) {
            return taskRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
        }

        private Discipline findDisciplineById(Integer id) {
            return disciplineRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Discipline", "id", id));
        }

        private void validateCreateDTO(CreateTaskDTO dto) {
            if (dto == null) {
                throw new BadRequestException("Task data is required");
            }
            if (dto.disciplineId() == null || dto.disciplineId() <= 0) {
                throw new BadRequestException("Valid discipline ID is required");
            }
            if (dto.title() == null || dto.title().isBlank()) {
                throw new BadRequestException("Task title is required");
            }
        }
    }