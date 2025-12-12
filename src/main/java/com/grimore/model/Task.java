package com.grimore.model;

import com.grimore.enums.TaskType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discipline_id", nullable = false)
    private Discipline discipline;

    @NotBlank(message = "Título é obrigatório")
    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Tipo da tarefa é obrigatório")
    @Column(nullable = false, length = 50)
    private TaskType type;

    @NotNull(message = "Data de entrega é obrigatória")
    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Column(nullable = false)
    @Builder.Default
    private Boolean completed = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}