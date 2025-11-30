package com.grimore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotBlank(message = "Nome é obrigatório")
    @Column(nullable = false)
    private String fullName;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Column(nullable = false)
    private String password;

    @Column(name = "university_name")
    @Size(max = 255, message = "Nome da universidade não pode exceder 255 caracteres")
    private String universityName;

    @Size(max = 255, message = "Nome do curso não pode exceder 255 caracteres")
    @Column(name = "course_name")
    private String courseName;

    @Min(value = 1, message = "Semestre deve ser maior que 0")
    @NotNull(message = "Semestre atual é obrigatório")
    @Column(name = "current_semester", nullable = false)
    private Integer currentSemester;
}