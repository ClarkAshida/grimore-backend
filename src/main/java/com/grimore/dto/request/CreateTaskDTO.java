package com.grimore.dto.request;

import com.grimore.enums.TaskPriority;
import com.grimore.enums.TaskStatus;
import com.grimore.enums.TaskType;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateTaskDTO(
    @NotNull(message = "ID da disciplina é obrigatório")
    UUID disciplineId,

    @NotBlank(message = "Título é obrigatório")
    @Size(min = 3, max = 200, message = "Título deve ter entre 3 e 200 caracteres")
    String title,

    String description,

    @NotNull(message = "Tipo da tarefa é obrigatório")
    TaskType taskType,

    TaskStatus status,
    TaskPriority priority,

    @NotNull(message = "Data de entrega é obrigatória")
    @Future(message = "Data de entrega deve ser futura")
    LocalDateTime dueDate,

    @Min(value = 0, message = "Peso da nota não pode ser negativo")
    Double gradeWeight,

    @Min(value = 0, message = "Nota obtida não pode ser negativa")
    Double gradeObtained
) {}