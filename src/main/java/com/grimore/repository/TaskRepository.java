package com.grimore.repository;

import com.grimore.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    Page<Task> findByCompleted(Boolean completed, Pageable pageable);

    List<Task> findByDisciplineId(Integer disciplineId);
    Page<Task> findByDisciplineId(Integer disciplineId, Pageable pageable);

    List<Task> findByDisciplineIdAndCompleted(Integer disciplineId, Boolean completed);
    Page<Task> findByDisciplineIdAndCompleted(Integer disciplineId, Boolean completed, Pageable pageable);

    List<Task> findByDiscipline_StudentId(Integer studentId);
    Page<Task> findByDiscipline_StudentId(Integer studentId, Pageable pageable);

    List<Task> findByDiscipline_StudentIdAndCompleted(Integer studentId, Boolean completed);
    Page<Task> findByDiscipline_StudentIdAndCompleted(Integer studentId, Boolean completed, Pageable pageable);

    // Métodos para buscar apenas tasks de disciplinas ativas
    List<Task> findByDiscipline_StudentIdAndDiscipline_ActiveTrue(Integer studentId);
    Page<Task> findByDiscipline_StudentIdAndDiscipline_ActiveTrue(Integer studentId, Pageable pageable);

    List<Task> findByDiscipline_StudentIdAndCompletedAndDiscipline_ActiveTrue(Integer studentId, Boolean completed);
    Page<Task> findByDiscipline_StudentIdAndCompletedAndDiscipline_ActiveTrue(Integer studentId, Boolean completed, Pageable pageable);
}
