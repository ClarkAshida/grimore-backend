package com.grimore.repository;

import com.grimore.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    Optional<Student> findByEmail(String email);
    List<Student> findByActiveTrue();
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Integer id);
}
