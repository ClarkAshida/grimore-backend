package com.grimore.model;

import com.grimore.enums.WorkloadHours;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
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

    @NotBlank(message = "Código da disciplina é obrigatório")
    @Column(length = 20)
    private String code;

    @NotBlank(message = "Código do horário é obrigatório")
    @Column(name = "schedule_code", length = 20)
    private String scheduleCode;

    @Column
    private String location;

    @Column(name = "color_hex", length = 7)
    private String colorHex;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Carga horária é obrigatória")
    @Column(name = "workload_hours", nullable = false)
    @Builder.Default
    private WorkloadHours workloadHours = WorkloadHours.H30;

    @Min(value = 0, message = "Contador de faltas não pode ser negativo")
    @Column(name = "absences_hours", nullable = false)
    @Builder.Default
    private Integer absencesHours = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}