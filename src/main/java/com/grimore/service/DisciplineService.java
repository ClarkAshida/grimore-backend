package com.grimore.service;

import com.grimore.dto.request.CreateDisciplineDTO;
import com.grimore.dto.response.DisciplineDTO;
import com.grimore.exception.resource.ConflictException;
import com.grimore.exception.resource.ResourceNotFoundException;
import com.grimore.exception.validation.BadRequestException;
import com.grimore.mapper.DisciplineMapper;
import com.grimore.model.Discipline;
import com.grimore.model.Student;
import com.grimore.repository.DisciplineRepository;
import com.grimore.repository.StudentRepository;
import com.grimore.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DisciplineService {
    private final DisciplineRepository disciplineRepository;
    private final StudentRepository studentRepository;
    private final DisciplineMapper mapper;

    @Transactional
    public DisciplineDTO create(CreateDisciplineDTO dto) {
        validateCreateDTO(dto);
        Integer currentStudentId = SecurityUtils.getCurrentStudentId();
        Student student = findStudentById(currentStudentId);
        validateDuplicateCode(currentStudentId, dto.code());

        try {
            Discipline discipline = mapper.toEntity(dto);
            discipline.setStudent(student);
            Discipline saved = disciplineRepository.save(discipline);

            log.info("Discipline created successfully for current student: {}", currentStudentId);
            return mapper.toDTO(saved);
        } catch (ConflictException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error creating discipline", ex);
            throw new BadRequestException("Failed to create discipline");
        }
    }

    @Transactional(readOnly = true)
    public DisciplineDTO findCurrentStudentDisciplineById(Integer id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid discipline ID");
        }

        Integer currentStudentId = SecurityUtils.getCurrentStudentId();
        Discipline discipline = findDisciplineById(id);

        if (!discipline.getStudent().getId().equals(currentStudentId)) {
            throw new BadRequestException("You don't have access to this discipline");
        }

        return mapper.toDTO(discipline);
    }

    @Transactional(readOnly = true)
    public List<DisciplineDTO> findCurrentStudentDisciplines(boolean activeOnly) {
        try {
            Integer currentStudentId = SecurityUtils.getCurrentStudentId();

            List<Discipline> disciplines = activeOnly
                    ? disciplineRepository.findByStudentIdAndActiveTrue(currentStudentId)
                    : disciplineRepository.findByStudentId(currentStudentId);

            log.info("Retrieved {} disciplines for current student", disciplines.size());
            return mapper.toDTO(disciplines);
        } catch (Exception ex) {
            log.error("Error fetching current student disciplines", ex);
            throw new BadRequestException("Failed to fetch disciplines");
        }
    }

    @Transactional
    public DisciplineDTO updateCurrentStudentDiscipline(Integer id, CreateDisciplineDTO dto) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid discipline ID");
        }

        validateCreateDTO(dto);
        Integer currentStudentId = SecurityUtils.getCurrentStudentId();
        Discipline discipline = findDisciplineById(id);

        if (!discipline.getStudent().getId().equals(currentStudentId)) {
            throw new BadRequestException("You don't have access to this discipline");
        }

        if (isCodeChanged(discipline, dto.code())) {
            validateDuplicateCode(currentStudentId, dto.code());
        }

        try {
            mapper.updateEntity(dto, discipline);
            Discipline updated = disciplineRepository.save(discipline);

            log.info("Discipline updated successfully: {}", id);
            return mapper.toDTO(updated);
        } catch (ConflictException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error updating discipline: {}", id, ex);
            throw new BadRequestException("Failed to update discipline");
        }
    }

    @Transactional
    public void deactivateCurrentStudentDiscipline(Integer id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid discipline ID");
        }

        try {
            Integer currentStudentId = SecurityUtils.getCurrentStudentId();
            Discipline discipline = findDisciplineById(id);

            if (!discipline.getStudent().getId().equals(currentStudentId)) {
                throw new BadRequestException("You don't have access to this discipline");
            }

            if (!discipline.getActive()) {
                throw new BadRequestException("Discipline is already inactive");
            }

            discipline.setActive(false);
            disciplineRepository.save(discipline);

            log.info("Discipline deactivated successfully: {}", id);
        } catch (ResourceNotFoundException | BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error deactivating discipline: {}", id, ex);
            throw new BadRequestException("Failed to deactivate discipline");
        }
    }

    // Métodos para Admin
    @Transactional(readOnly = true)
    public DisciplineDTO findById(Integer id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid discipline ID");
        }

        Discipline discipline = findDisciplineById(id);
        return mapper.toDTO(discipline);
    }

    @Transactional(readOnly = true)
    public List<DisciplineDTO> findByStudentId(Integer studentId, boolean activeOnly) {
        if (studentId == null || studentId <= 0) {
            throw new BadRequestException("Invalid student ID");
        }

        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student", "id", studentId);
        }

        try {
            List<Discipline> disciplines = activeOnly
                    ? disciplineRepository.findByStudentIdAndActiveTrue(studentId)
                    : disciplineRepository.findByStudentId(studentId);
            return mapper.toDTO(disciplines);
        } catch (Exception ex) {
            log.error("Error fetching disciplines for student: {}", studentId, ex);
            throw new BadRequestException("Failed to fetch disciplines");
        }
    }

    @Transactional
    public void deactivate(Integer id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid discipline ID");
        }

        try {
            Discipline discipline = findDisciplineById(id);

            if (!discipline.getActive()) {
                throw new BadRequestException("Discipline is already inactive");
            }

            discipline.setActive(false);
            disciplineRepository.save(discipline);

            log.info("Discipline deactivated successfully: {}", id);
        } catch (ResourceNotFoundException | BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error deactivating discipline: {}", id, ex);
            throw new BadRequestException("Failed to deactivate discipline");
        }
    }

    private Discipline findDisciplineById(Integer id) {
        return disciplineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discipline", "id", id));
    }

    private Student findStudentById(Integer studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));
    }

    private void validateDuplicateCode(Integer studentId, String code) {
        if (code != null && disciplineRepository.existsByStudentIdAndCodeAndActiveTrue(studentId, code)) {
            throw new ConflictException("Active discipline with code '" + code + "' already exists for this student");
        }
    }

    private boolean isCodeChanged(Discipline discipline, String newCode) {
        return newCode != null && !newCode.equals(discipline.getCode());
    }

    private void validateCreateDTO(CreateDisciplineDTO dto) {
        if (dto == null) {
            throw new BadRequestException("Discipline data is required");
        }
        if (dto.name() == null || dto.name().isBlank()) {
            throw new BadRequestException("Discipline name is required");
        }
    }
}