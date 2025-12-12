package com.grimore.repository;

import com.grimore.model.Discipline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DisciplineRepository extends JpaRepository<Discipline, UUID> {
    List<Discipline> findByStudentId(UUID studentId);
    List<Discipline> findByStudentIdAndActive(UUID studentId, Boolean active);
}
