package com.grimore.dto.request;

import jakarta.validation.constraints.*;

public record CreateStudentDTO(
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    String fullName,

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    String email,

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    String password,

    String universityName,
    String courseName,

    @NotNull(message = "Semestre atual é obrigatório")
    @Min(value = 1, message = "Semestre deve ser no mínimo 1")
    Integer currentSemester
) {}
