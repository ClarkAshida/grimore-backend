package com.grimore.repository;

import com.grimore.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    Optional<Student> findByEmail(String email);
    List<Student> findByActiveTrue();
    Page<Student> findByActiveTrue(Pageable pageable);
    Page<Student> findAll(Pageable pageable);
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Integer id);
}
