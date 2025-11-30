package com.grimore.model;

import com.grimore.enums.TaskPriority;
import com.grimore.enums.TaskStatus;
import com.grimore.enums.TaskType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discipline_id", nullable = false)
    private Discipline discipline;

    @NotBlank(message = "Título é obrigatório")
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Tipo da tarefa é obrigatório")
    @Column(name = "task_type", nullable = false)
    private TaskType taskType;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status é obrigatório")
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.TODO;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Prioridade é obrigatória")
    @Column(nullable = false)
    private TaskPriority priority = TaskPriority.MEDIUM;

    @NotNull(message = "Data de entrega é obrigatória")
    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Min(value = 0, message = "Peso da nota não pode ser negativo")
    @Max(value = 10, message = "Peso da nota não pode ser maior que 10")
    @Column(name = "grade_weight")
    private Double gradeWeight;

    @Min(value = 0, message = "Nota obtida não pode ser negativa")
    @Max(value = 10, message = "Nota obtida não pode ser maior que 10")
    @Column(name = "grade_obtained")
    private Double gradeObtained;
}