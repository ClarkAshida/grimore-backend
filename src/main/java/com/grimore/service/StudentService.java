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
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
            throw new BadRequestException("Falha ao criar estudante");
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
            throw new BadRequestException("Falha ao buscar perfil");
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
            throw new BadRequestException("Falha ao atualizar perfil");
        }
    }

    @Transactional
    public void deactivateCurrentProfile() {
        try {
            Student student = SecurityUtils.getCurrentStudent();

            if (!student.getActive()) {
                throw new BadRequestException("Conta já está inativa");
            }

            student.setActive(false);
            studentRepository.save(student);

            log.info("Profile deactivated successfully: {}", student.getEmail());
        } catch (BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error deactivating profile", ex);
            throw new BadRequestException("Falha ao desativar perfil");
        }
    }

    // Métodos para Admin
    @Transactional(readOnly = true)
    public StudentDTO findById(Integer id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("ID de estudante inválido");
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
            throw new BadRequestException("Falha ao buscar estudantes");
        }
    }

    @Transactional(readOnly = true)
    public Page<StudentDTO> findAll(boolean activeOnly, @NotNull Pageable pageable) {
        try {
            Page<Student> students = activeOnly
                    ? studentRepository.findByActiveTrue(pageable)
                    : studentRepository.findAll(pageable);
            log.info("Retrieved {} students", students.getTotalElements());
            return students.map(mapper::toDTO);
        } catch (Exception ex) {
            log.error("Error fetching students", ex);
            throw new BadRequestException("Falha ao buscar estudantes");
        }
    }

    @Transactional
    public void deactivate(Integer id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("ID de estudante inválido");
        }

        try {
            Student student = findStudentById(id);

            if (!student.getActive()) {
                throw new BadRequestException("Estudante já está inativo");
            }

            student.setActive(false);
            studentRepository.save(student);

            log.info("Student deactivated successfully: {}", id);
        } catch (ResourceNotFoundException | BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error deactivating student: {}", id, ex);
            throw new BadRequestException("Falha ao desativar estudante");
        }
    }

    @Transactional
    public void delete(Integer id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("ID de estudante inválido");
        }

        try {
            if (!studentRepository.existsById(id)) {
                throw new ResourceNotFoundException("Estudante", "id", id);
            }

            studentRepository.deleteById(id);
            log.info("Student deleted successfully: {}", id);
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error deleting student: {}", id, ex);
            throw new BadRequestException("Falha ao deletar estudante");
        }
    }

    private Student findStudentById(Integer id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estudante", "id", id));
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
            throw new BadRequestException("Dados do estudante são obrigatórios");
        }
        if (dto.email() == null || dto.email().isBlank()) {
            throw new BadRequestException("Email é obrigatório");
        }
        if (dto.fullName() == null || dto.fullName().isBlank()) {
            throw new BadRequestException("Nome completo é obrigatório");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new InvalidPasswordException("Senha é obrigatória");
        }
        if (password.length() < 8) {
            throw new InvalidPasswordException("Senha deve ter no mínimo 8 caracteres");
        }
    }
}