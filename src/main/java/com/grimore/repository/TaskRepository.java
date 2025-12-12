package com.grimore.repository;

import com.grimore.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findByDisciplineId(Integer disciplineId);
    List<Task> findByDisciplineIdAndCompleted(Integer disciplineId, Boolean completed);
}
