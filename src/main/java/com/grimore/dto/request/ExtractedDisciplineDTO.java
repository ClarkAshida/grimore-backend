package com.grimore.dto.request;

import com.grimore.enums.WorkloadHours;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ExtractedDisciplineDTO (
        @NotBlank(message = "Nome da disciplina é obrigatório")
        @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
        String name,

        @NotBlank(message = "Código da disciplina é obrigatório")
        @Pattern(regexp = "^[A-Z]{3}[0-9]{4}$", message = "Código deve conter 3 letras maiúsculas seguidas de 4 números (ex: IMD1012)")
        String code,

        @NotBlank(message = "Código do horário é obrigatório")
        @Pattern(regexp = "^[1-7]+[MVN][1-6]+$", message = "Formato de horário inválido. Use o padrão UFRN: dias(1-7) + turno(M/V/N) + slots(1-6) (ex: 246N12)")
        String scheduleCode,

        String location,

        WorkloadHours workloadHours
) {}