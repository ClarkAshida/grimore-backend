package com.grimore.service;

import com.grimore.dto.request.CreateStudentDTO;
import com.grimore.dto.response.StudentDTO;
import com.grimore.mapper.StudentMapper;
import com.grimore.model.Student;
import com.grimore.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper mapper;

    @Transactional
    public StudentDTO create(CreateStudentDTO dto) {
        validateDuplicateEmail(dto.email());

        Student student = mapper.toEntity(dto);
        return mapper.toDTO(studentRepository.save(student));
    }

    @Transactional(readOnly = true)
    public StudentDTO findById(Integer id) {
        Student student = findStudentById(id);
        return mapper.toDTO(student);
    }

    @Transactional(readOnly = true)
    public List<StudentDTO> findAll(boolean activeOnly) {
        List<Student> students = activeOnly
                ? studentRepository.findByActiveTrue()
                : studentRepository.findAll();
        return mapper.toDTO(students);
    }

    @Transactional
    public StudentDTO update(Integer id, CreateStudentDTO dto) {
        Student student = findStudentById(id);

        if (isEmailChanged(student, dto.email())) {
            validateDuplicateEmailForUpdate(dto.email(), id);
        }

        mapper.updateEntity(dto, student);
        return mapper.toDTO(studentRepository.save(student));
    }

    @Transactional
    public void deactivate(Integer id) {
        Student student = findStudentById(id);
        student.setActive(false);
        studentRepository.save(student);
    }

    @Transactional
    public void delete(Integer id) {
        if (!studentRepository.existsById(id)) {
            throw new EntityNotFoundException("Estudante não encontrado");
        }
        studentRepository.deleteById(id);
    }

    private Student findStudentById(Integer id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estudante não encontrado"));
    }

    private void validateDuplicateEmail(String email) {
        if (studentRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Já existe um estudante cadastrado com este email");
        }
    }

    private void validateDuplicateEmailForUpdate(String email, Integer id) {
        if (studentRepository.existsByEmailAndIdNot(email, id)) {
            throw new IllegalArgumentException("Já existe um estudante cadastrado com este email");
        }
    }

    private boolean isEmailChanged(Student student, String newEmail) {
        return newEmail != null && !newEmail.equals(student.getEmail());
    }
}
