package com.grimore.repository;

import com.grimore.model.Discipline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisciplineRepository extends JpaRepository<Discipline, Integer> {
    List<Discipline> findByStudentId(Integer studentId);
    List<Discipline> findByStudentIdAndActive(Integer studentId, Boolean active);
    List<Discipline> findByStudentIdAndActiveTrue(Integer studentId);
    boolean existsByStudentIdAndCodeAndActiveTrue(Integer studentId, String code);
}
