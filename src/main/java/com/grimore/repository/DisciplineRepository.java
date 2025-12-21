package com.grimore.repository;

import com.grimore.model.Discipline;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.List;

@Repository
public interface DisciplineRepository extends JpaRepository<Discipline, @NotNull Integer> {
    @NotNull
    List<Discipline> findByStudentId(@NotNull Integer studentId);

    @NotNull
    List<Discipline> findByStudentIdAndActive(@NotNull Integer studentId, @NotNull Boolean active);

    @NotNull
    List<Discipline> findByStudentIdAndActiveTrue(@NotNull Integer studentId);

    boolean existsByStudentIdAndCodeAndActiveTrue(@NotNull Integer studentId, @NotNull String code);

    @NotNull
    List<Discipline> findByStudentIdAndActiveTrueAndIdNot(@NotNull Integer studentId, @NotNull Integer disciplineId);

    @NotNull
    Page<Discipline> findByStudentIdAndActiveTrue(@NotNull Integer studentId, @NotNull Pageable pageable);

    @NotNull
    Page<Discipline> findByStudentId(@NotNull Integer studentId, @NotNull Pageable pageable);
}
