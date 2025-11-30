package com.grimore.dto.request;

import com.grimore.enums.DisciplineNature;
import com.grimore.enums.DisciplineStatus;
import com.grimore.enums.TotalHours;
import jakarta.validation.constraints.*;

import java.util.UUID;

public record CreateDisciplineDTO(
    @NotNull(message = "ID do estudante é obrigatório")
    UUID studentId,

    @NotBlank(message = "Nome da disciplina é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    String name,

    @Pattern(regexp = "^[A-Z]{3}[0-9]{4}$", message = "Código deve conter 3 letras maiúsculas seguidas de 4 números")
    String code,
    String location,

    @NotNull(message = "Natureza da disciplina é obrigatória")
    DisciplineNature nature,

    @NotNull(message = "Semestre é obrigatório")
    @Min(value = 1, message = "Semestre deve ser maior que 0")
    Integer semester,

    DisciplineStatus status,

    @NotNull(message = "Carga horária é obrigatória")
    @Min(value = 1, message = "Carga horária deve ser maior que 0")
    TotalHours totalHours,

    @NotNull(message = "Horários das aulas são obrigatórios")
    @Pattern(regexp = "^[2-7][MNV][1-6]$", message = "Formato de horário inválido. Use o padrão: dia(2-7) + turno(M/N/V) + horário(1-6)")
    String classSchedules
) {}