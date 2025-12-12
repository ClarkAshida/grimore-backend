package com.grimore.dto.request;

import com.grimore.enums.TaskType;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record CreateTaskDTO(
    @NotNull(message = "ID da disciplina é obrigatório")
    Integer disciplineId,

    @NotBlank(message = "Título é obrigatório")
    @Size(min = 3, max = 200, message = "Título deve ter entre 3 e 200 caracteres")
    String title,

    @NotNull(message = "Tipo da tarefa é obrigatório")
    TaskType type,

    @NotNull(message = "Data de entrega é obrigatória")
    LocalDateTime dueDate
) {}