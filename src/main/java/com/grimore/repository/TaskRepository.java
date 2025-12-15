package com.grimore.repository;

import com.grimore.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findByDisciplineId(Integer disciplineId);
    List<Task> findByDisciplineIdAndCompleted(Integer disciplineId, Boolean completed);
    List<Task> findByDiscipline_StudentId(Integer studentId);
    List<Task> findByDiscipline_StudentIdAndCompleted(Integer studentId, Boolean completed);

    // Métodos para buscar apenas tasks de disciplinas ativas
    List<Task> findByDiscipline_StudentIdAndDiscipline_ActiveTrue(Integer studentId);
    List<Task> findByDiscipline_StudentIdAndCompletedAndDiscipline_ActiveTrue(Integer studentId, Boolean completed);
}
