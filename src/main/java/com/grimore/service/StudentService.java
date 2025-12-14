package com.grimore.service;

import com.grimore.dto.request.CreateStudentDTO;
import com.grimore.dto.response.StudentDTO;
import com.grimore.exception.resource.ResourceNotFoundException;
import com.grimore.exception.user.EmailAlreadyExistsException;
import com.grimore.exception.validation.BadRequestException;
import com.grimore.exception.validation.InvalidPasswordException;
import com.grimore.mapper.StudentMapper;
import com.grimore.model.Student;
import com.grimore.repository.StudentRepository;
import com.grimore.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public StudentDTO create(CreateStudentDTO dto) {
        validateCreateDTO(dto);
        validateDuplicateEmail(dto.email());
        validatePassword(dto.password());

        try {
            Student student = mapper.toEntity(dto);
            student.setPassword(passwordEncoder.encode(student.getPassword()));
            Student saved = studentRepository.save(student);

            log.info("Student created successfully: {}", saved.getEmail());
            return mapper.toDTO(saved);
        } catch (EmailAlreadyExistsException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error creating student", ex);
            throw new BadRequestException("Failed to create student");
        }
    }

    @Transactional(readOnly = true)
    public StudentDTO getCurrentProfile() {
        try {
            Student student = SecurityUtils.getCurrentStudent();
            log.info("Profile retrieved for student: {}", student.getEmail());
            return mapper.toDTO(student);
        } catch (Exception ex) {
            log.error("Error retrieving current profile", ex);
            throw new BadRequestException("Failed to retrieve profile");
        }
    }

    @Transactional
    public StudentDTO updateCurrentProfile(CreateStudentDTO dto) {
        validateCreateDTO(dto);
        Student student = SecurityUtils.getCurrentStudent();

        if (isEmailChanged(student, dto.email())) {
            validateDuplicateEmailForUpdate(dto.email(), student.getId());
        }

        try {
            mapper.updateEntity(dto, student);
            Student updated = studentRepository.save(student);

            log.info("Profile updated successfully: {}", updated.getEmail());
            return mapper.toDTO(updated);
        } catch (EmailAlreadyExistsException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error updating profile", ex);
            throw new BadRequestException("Failed to update profile");
        }
    }

    @Transactional
    public void deactivateCurrentProfile() {
        try {
            Student student = SecurityUtils.getCurrentStudent();

            if (!student.getActive()) {
                throw new BadRequestException("Account is already inactive");
            }

            student.setActive(false);
            studentRepository.save(student);

            log.info("Profile deactivated successfully: {}", student.getEmail());
        } catch (BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error deactivating profile", ex);
            throw new BadRequestException("Failed to deactivate profile");
        }
    }

    // Métodos para Admin
    @Transactional(readOnly = true)
    public StudentDTO findById(Integer id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid student ID");
        }

        Student student = findStudentById(id);
        return mapper.toDTO(student);
    }

    @Transactional(readOnly = true)
    public List<StudentDTO> findAll(boolean activeOnly) {
        try {
            List<Student> students = activeOnly
                    ? studentRepository.findByActiveTrue()
                    : studentRepository.findAll();
            return mapper.toDTO(students);
        } catch (Exception ex) {
            log.error("Error fetching students", ex);
            throw new BadRequestException("Failed to fetch students");
        }
    }

    @Transactional
    public void deactivate(Integer id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid student ID");
        }

        try {
            Student student = findStudentById(id);

            if (!student.getActive()) {
                throw new BadRequestException("Student is already inactive");
            }

            student.setActive(false);
            studentRepository.save(student);

            log.info("Student deactivated successfully: {}", id);
        } catch (ResourceNotFoundException | BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error deactivating student: {}", id, ex);
            throw new BadRequestException("Failed to deactivate student");
        }
    }

    @Transactional
    public void delete(Integer id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid student ID");
        }

        try {
            if (!studentRepository.existsById(id)) {
                throw new ResourceNotFoundException("Student", "id", id);
            }

            studentRepository.deleteById(id);
            log.info("Student deleted successfully: {}", id);
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error deleting student: {}", id, ex);
            throw new BadRequestException("Failed to delete student");
        }
    }

    private Student findStudentById(Integer id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
    }

    private void validateDuplicateEmail(String email) {
        if (studentRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }
    }

    private void validateDuplicateEmailForUpdate(String email, Integer id) {
        if (studentRepository.existsByEmailAndIdNot(email, id)) {
            throw new EmailAlreadyExistsException(email);
        }
    }

    private boolean isEmailChanged(Student student, String newEmail) {
        return newEmail != null && !newEmail.equals(student.getEmail());
    }

    private void validateCreateDTO(CreateStudentDTO dto) {
        if (dto == null) {
            throw new BadRequestException("Student data is required");
        }
        if (dto.email() == null || dto.email().isBlank()) {
            throw new BadRequestException("Email is required");
        }
        if (dto.fullName() == null || dto.fullName().isBlank()) {
            throw new BadRequestException("Full name is required");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new InvalidPasswordException("Password is required");
        }
        if (password.length() < 8) {
            throw new InvalidPasswordException("Password must be at least 8 characters long");
        }
    }
}