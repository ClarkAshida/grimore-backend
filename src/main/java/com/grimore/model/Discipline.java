package com.grimore.model;

import com.grimore.enums.DisciplineNature;
import com.grimore.enums.DisciplineStatus;
import com.grimore.enums.TotalHours;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "disciplines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Discipline {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NotBlank(message = "Nome da disciplina é obrigatório")
    @Column(nullable = false)
    private String name;

    @Column
    private String code;

    @Column
    private String location;

    @Column
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Natureza da disciplina é obrigatória")
    DisciplineNature nature = DisciplineNature.OBLIGATORY;

    @NotNull(message = "Semestre é obrigatório")
    @Min(value = 1, message = "Semestre deve ser maior que 0")
    @Column(nullable = false)
    private Integer semester;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status da disciplina é obrigatório")
    @Column(nullable = false)
    private DisciplineStatus status = DisciplineStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Carga horária é obrigatória")
    @Column(name = "total_hours", nullable = false)
    private TotalHours totalHours = TotalHours.H30;

    @Min(value = 0, message = "Contador de faltas não pode ser negativo")
    @Column(name = "absences_count", nullable = false)
    @Builder.Default
    private Integer absencesCount = 0;

    @NotNull(message = "Horários das aulas são obrigatórios")
    @Column(name = "class_schedules")
    private String classSchedules;
}