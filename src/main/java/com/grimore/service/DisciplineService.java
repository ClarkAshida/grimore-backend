package com.grimore.service;

import com.grimore.dto.request.CreateDisciplineDTO;
import com.grimore.dto.request.ExtractedDisciplineDTO;
import com.grimore.dto.response.BatchCreateReportDTO;
import com.grimore.dto.response.DisciplineDTO;
import com.grimore.dto.response.DisciplineSummaryDTO;
import com.grimore.enums.WorkloadHours;
import com.grimore.exception.resource.ConflictException;
import com.grimore.exception.resource.ResourceNotFoundException;
import com.grimore.exception.validation.BadRequestException;
import com.grimore.mapper.DisciplineMapper;
import com.grimore.model.Discipline;
import com.grimore.model.Student;
import com.grimore.repository.DisciplineRepository;
import com.grimore.repository.StudentRepository;
import com.grimore.security.SecurityUtils;
import com.grimore.util.ScheduleCodeParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DisciplineService {
    private final DisciplineRepository disciplineRepository;
    private final StudentRepository studentRepository;
    private final DisciplineMapper mapper;

    @Transactional
    public BatchCreateReportDTO createBatchFromExtractedWithReport(List<ExtractedDisciplineDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            throw new BadRequestException("Lista de disciplinas não pode ser vazia");
        }

        Integer currentStudentId = SecurityUtils.getCurrentStudentId();
        Student student = findStudentById(currentStudentId);

        List<DisciplineDTO> createdDisciplines = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < dtos.size(); i++) {
            ExtractedDisciplineDTO dto = dtos.get(i);
            try {
                validateExtractedDTO(dto);

                String code = dto.code().trim().toUpperCase();
                String schedule = dto.scheduleCode().trim().toUpperCase().replaceAll("\\s+", " ");
                String name = dto.name().trim();
                String location = dto.location() != null ? dto.location().trim() : null;

                ExtractedDisciplineDTO normalized = new ExtractedDisciplineDTO(
                        name, code, schedule, location, dto.workloadHours()
                );

                validateDuplicateCode(currentStudentId, normalized.code());
                validateScheduleCode(normalized.scheduleCode());
                verifyScheduleConflict(currentStudentId, normalized.scheduleCode(), null);

                WorkloadHours workload = normalized.workloadHours() != null
                        ? normalized.workloadHours()
                        : ScheduleCodeParser.inferWorkloadFromScheduleCode(normalized.scheduleCode());

                CreateDisciplineDTO createDTO = new CreateDisciplineDTO(
                        normalized.name(),
                        normalized.code(),
                        normalized.scheduleCode(),
                        normalized.location(),
                        "#6366F1",
                        workload
                );

                Discipline discipline = mapper.toEntity(createDTO);
                discipline.setStudent(student);

                Discipline saved = disciplineRepository.save(discipline);
                createdDisciplines.add(mapper.toDTO(saved));

            } catch (Exception ex) {
                String error = String.format("Disciplina %d (%s): %s",
                        i + 1,
                        dto.code() != null ? dto.code() : "sem código",
                        ex.getMessage()
                );
                errors.add(error);
            }
        }

        if (createdDisciplines.isEmpty()) {
            throw new BadRequestException("Nenhuma disciplina pôde ser criada. Erros: " + String.join("; ", errors));
        }

        return new BatchCreateReportDTO(createdDisciplines, errors);
    }

    // ==================== Métodos para Estudante Autenticado ====================

    /**
     * Cria uma disciplina manualmente para o estudante autenticado.
     */
    @Transactional
    public DisciplineDTO create(CreateDisciplineDTO dto) {
        validateCreateDTO(dto);
        Integer currentStudentId = SecurityUtils.getCurrentStudentId();
        Student student = findStudentById(currentStudentId);

        validateDuplicateCode(currentStudentId, dto.code());
        validateScheduleCode(dto.scheduleCode());
        verifyScheduleConflict(currentStudentId, dto.scheduleCode(), null);

        try {
            Discipline discipline = mapper.toEntity(dto);
            discipline.setStudent(student);
            Discipline saved = disciplineRepository.save(discipline);

            log.info("Discipline created successfully for student {}: {} ({})",
                    currentStudentId, saved.getName(), saved.getCode());
            return mapper.toDTO(saved);
        } catch (ConflictException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error creating discipline for student {}", currentStudentId, ex);
            throw new BadRequestException("Falha ao criar disciplina");
        }
    }

    /**
     * Cria uma disciplina a partir de dados extraídos (ex: upload de PDF).
     * Infere automaticamente a carga horária se não fornecida.
     */
    @Transactional
    public DisciplineDTO createFromExtracted(ExtractedDisciplineDTO dto) {
        validateExtractedDTO(dto);
        Integer currentStudentId = SecurityUtils.getCurrentStudentId();
        Student student = findStudentById(currentStudentId);

        validateDuplicateCode(currentStudentId, dto.code());
        validateScheduleCode(dto.scheduleCode());
        verifyScheduleConflict(currentStudentId, dto.scheduleCode(), null);

        try {
            // Inferir carga horária se não fornecida
            WorkloadHours workload = dto.workloadHours() != null
                    ? dto.workloadHours()
                    : ScheduleCodeParser.inferWorkloadFromScheduleCode(dto.scheduleCode());

            log.debug("Workload inferred for {}: {} (from schedule: {})",
                    dto.code(), workload, dto.scheduleCode());

            CreateDisciplineDTO createDTO = new CreateDisciplineDTO(
                    dto.name(),
                    dto.code(),
                    dto.scheduleCode(),
                    dto.location(),
                    "#6366F1", // cor padrão
                    workload
            );

            Discipline discipline = mapper.toEntity(createDTO);
            discipline.setStudent(student);
            Discipline saved = disciplineRepository.save(discipline);

            log.info("Discipline created from extraction for student {}: {} - {} (workload: {})",
                    currentStudentId, saved.getCode(), saved.getName(), workload);
            return mapper.toDTO(saved);
        } catch (ConflictException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error creating discipline from extraction for student {}", currentStudentId, ex);
            throw new BadRequestException("Falha ao criar disciplina a partir do comprovante");
        }
    }

    /**
     * Cria múltiplas disciplinas em lote a partir de dados extraídos.
     * Útil para processar comprovante de matrícula com várias disciplinas.
     */
    @Transactional
    public List<DisciplineDTO> createBatchFromExtracted(List<ExtractedDisciplineDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            throw new BadRequestException("Lista de disciplinas não pode ser vazia");
        }

        Integer currentStudentId = SecurityUtils.getCurrentStudentId();
        Student student = findStudentById(currentStudentId);

        List<DisciplineDTO> createdDisciplines = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < dtos.size(); i++) {
            ExtractedDisciplineDTO dto = dtos.get(i);
            try {
                validateExtractedDTO(dto);
                validateDuplicateCode(currentStudentId, dto.code());
                validateScheduleCode(dto.scheduleCode());
                verifyScheduleConflict(currentStudentId, dto.scheduleCode(), null);

                WorkloadHours workload = dto.workloadHours() != null
                        ? dto.workloadHours()
                        : ScheduleCodeParser.inferWorkloadFromScheduleCode(dto.scheduleCode());

                CreateDisciplineDTO createDTO = new CreateDisciplineDTO(
                        dto.name(),
                        dto.code(),
                        dto.scheduleCode(),
                        dto.location(),
                        "#6366F1",
                        workload
                );

                Discipline discipline = mapper.toEntity(createDTO);
                discipline.setStudent(student);
                Discipline saved = disciplineRepository.save(discipline);
                createdDisciplines.add(mapper.toDTO(saved));

                log.debug("Batch creation - discipline {} created successfully", dto.code());
            } catch (Exception ex) {
                String error = String.format("Disciplina %d (%s): %s",
                        i + 1, dto.code() != null ? dto.code() : "sem código", ex.getMessage());
                errors.add(error);
                log.warn("Error in batch creation: {}", error);
            }
        }

        if (!errors.isEmpty() && createdDisciplines.isEmpty()) {
            throw new BadRequestException("Nenhuma disciplina pôde ser criada. Erros: " + String.join("; ", errors));
        }

        if (!errors.isEmpty()) {
            log.warn("Batch creation completed with {} errors out of {} disciplines",
                    errors.size(), dtos.size());
        }

        log.info("Batch creation completed for student {}: {} disciplines created, {} errors",
                currentStudentId, createdDisciplines.size(), errors.size());

        return createdDisciplines;
    }

    /**
     * Busca disciplina específica do estudante autenticado por ID.
     */
    @Transactional(readOnly = true)
    public DisciplineDTO findCurrentStudentDisciplineById(Integer id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("ID de disciplina inválido");
        }

        Integer currentStudentId = SecurityUtils.getCurrentStudentId();
        Discipline discipline = findDisciplineById(id);

        if (!discipline.getStudent().getId().equals(currentStudentId)) {
            throw new BadRequestException("Você não tem acesso a esta disciplina");
        }

        return mapper.toDTO(discipline);
    }

    /**
     * Lista todas as disciplinas do estudante autenticado.
     */
    @Transactional(readOnly = true)
    public Page<DisciplineSummaryDTO> findCurrentStudentDisciplines(boolean activeOnly, Pageable pageable) {
        try {
            Integer currentStudentId = SecurityUtils.getCurrentStudentId();

            Page<Discipline> disciplines = activeOnly
                    ? disciplineRepository.findByStudentIdAndActiveTrue(currentStudentId, pageable)
                    : disciplineRepository.findByStudentId(currentStudentId, pageable);

            log.info("Retrieved {} disciplines for current student", disciplines.getTotalElements());
            return disciplines.map(mapper::toSummaryDTO);
        } catch (Exception ex) {
            log.error("Error fetching current student disciplines", ex);
            throw new BadRequestException("Falha ao buscar disciplinas");
        }
    }

    /**
     * Atualiza uma disciplina do estudante autenticado.
     */
    @Transactional
    public DisciplineDTO updateCurrentStudentDiscipline(Integer id, CreateDisciplineDTO dto) {
        if (id == null || id <= 0) {
            throw new BadRequestException("ID de disciplina inválido");
        }

        validateCreateDTO(dto);
        Integer currentStudentId = SecurityUtils.getCurrentStudentId();
        Discipline discipline = findDisciplineById(id);

        if (!discipline.getStudent().getId().equals(currentStudentId)) {
            throw new BadRequestException("Você não tem acesso a esta disciplina");
        }

        if (isCodeChanged(discipline, dto.code())) {
            validateDuplicateCode(currentStudentId, dto.code());
        }

        verifyScheduleConflict(currentStudentId, dto.scheduleCode(), id);

        try {
            mapper.updateEntity(dto, discipline);
            Discipline updated = disciplineRepository.save(discipline);

            log.info("Discipline {} updated successfully by student {}", id, currentStudentId);
            return mapper.toDTO(updated);
        } catch (ConflictException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error updating discipline {}", id, ex);
            throw new BadRequestException("Falha ao atualizar disciplina");
        }
    }

    /**
     * Desativa (soft delete) uma disciplina do estudante autenticado.
     */
    @Transactional
    public void deactivateCurrentStudentDiscipline(Integer id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("ID de disciplina inválido");
        }

        try {
            Integer currentStudentId = SecurityUtils.getCurrentStudentId();
            Discipline discipline = findDisciplineById(id);

            if (!discipline.getStudent().getId().equals(currentStudentId)) {
                throw new BadRequestException("Você não tem acesso a esta disciplina");
            }

            if (!discipline.getActive()) {
                throw new BadRequestException("Disciplina já está inativa");
            }

            discipline.setActive(false);
            disciplineRepository.save(discipline);

            log.info("Discipline {} deactivated successfully by student {}", id, currentStudentId);
        } catch (ResourceNotFoundException | BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error deactivating discipline {}", id, ex);
            throw new BadRequestException("Falha ao desativar disciplina");
        }
    }

    // ==================== Métodos para Admin ====================

    /**
     * Busca disciplina por ID (acesso admin).
     */
    @Transactional(readOnly = true)
    public DisciplineDTO findById(Integer id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("ID de disciplina inválido");
        }

        Discipline discipline = findDisciplineById(id);
        return mapper.toDTO(discipline);
    }

    /**
     * Lista disciplinas de um estudante específico (acesso admin).
     */
    @Transactional(readOnly = true)
    public List<DisciplineDTO> findByStudentId(Integer studentId, boolean activeOnly) {
        if (studentId == null || studentId <= 0) {
            throw new BadRequestException("ID de estudante inválido");
        }

        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Estudante", "id", studentId);
        }

        try {
            List<Discipline> disciplines = activeOnly
                    ? disciplineRepository.findByStudentIdAndActiveTrue(studentId)
                    : disciplineRepository.findByStudentId(studentId);

            log.info("Admin retrieved {} disciplines for student {}", disciplines.size(), studentId);
            return mapper.toDTO(disciplines);
        } catch (Exception ex) {
            log.error("Error fetching disciplines for student {}", studentId, ex);
            throw new BadRequestException("Falha ao buscar disciplinas");
        }
    }

    /**
     * Desativa disciplina (acesso admin).
     */
    @Transactional
    public void deactivate(Integer id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("ID de disciplina inválido");
        }

        try {
            Discipline discipline = findDisciplineById(id);

            if (!discipline.getActive()) {
                throw new BadRequestException("Disciplina já está inativa");
            }

            discipline.setActive(false);
            disciplineRepository.save(discipline);

            log.info("Discipline {} deactivated by admin", id);
        } catch (ResourceNotFoundException | BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error deactivating discipline {}", id, ex);
            throw new BadRequestException("Falha ao desativar disciplina");
        }
    }

    // ==================== Métodos Privados de Validação ====================

    /**
     * Verifica se há conflito de horário com outras disciplinas ativas do estudante.
     * Utiliza o ScheduleCodeParser refatorado para análise de slots.
     */
    private void verifyScheduleConflict(Integer studentId, String scheduleCode, Integer excludeDisciplineId) {
        if (scheduleCode == null || scheduleCode.isBlank()) {
            return;
        }

        List<Discipline> activeDisciplines = excludeDisciplineId == null
                ? disciplineRepository.findByStudentIdAndActiveTrue(studentId)
                : disciplineRepository.findByStudentIdAndActiveTrueAndIdNot(studentId, excludeDisciplineId);

        for (Discipline discipline : activeDisciplines) {
            if (ScheduleCodeParser.hasConflict(scheduleCode, discipline.getScheduleCode())) {
                // Obtém informações detalhadas para mensagem de erro mais clara
                var newScheduleInfo = ScheduleCodeParser.parseScheduleCode(scheduleCode);
                var existingScheduleInfo = ScheduleCodeParser.parseScheduleCode(discipline.getScheduleCode());

                throw new ConflictException(
                        String.format("Conflito de horário detectado com a disciplina '%s' (%s). " +
                                        "Novo horário: %s às %s. Horário existente: %s às %s.",
                                discipline.getName(),
                                discipline.getCode(),
                                newScheduleInfo.getDaysDescription(),
                                newScheduleInfo.getShiftsDescription(),
                                existingScheduleInfo.getDaysDescription(),
                                existingScheduleInfo.getShiftsDescription())
                );
            }
        }

        log.debug("No schedule conflict found for schedule code: {}", scheduleCode);
    }

    /**
     * Valida o formato do código de horário usando o ScheduleCodeParser.
     */
    private void validateScheduleCode(String scheduleCode) {
        if (scheduleCode == null || scheduleCode.isBlank()) {
            throw new BadRequestException("Código de horário é obrigatório");
        }

        if (!ScheduleCodeParser.isValidScheduleCode(scheduleCode)) {
            throw new BadRequestException(
                    "Código de horário inválido: " + scheduleCode +
                            ". Formato esperado: dias(2-6) + turno(M/T/N) + blocos(1-6). Ex: 246N12"
            );
        }
    }

    private Discipline findDisciplineById(Integer id) {
        return disciplineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina", "id", id));
    }

    private Student findStudentById(Integer studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudante", "id", studentId));
    }

    private void validateDuplicateCode(Integer studentId, String code) {
        if (code != null && disciplineRepository.existsByStudentIdAndCodeAndActiveTrue(studentId, code)) {
            throw new ConflictException(
                    "Disciplina ativa com código '" + code + "' já existe para este estudante"
            );
        }
    }

    private boolean isCodeChanged(Discipline discipline, String newCode) {
        return newCode != null && !newCode.equals(discipline.getCode());
    }

    private void validateCreateDTO(CreateDisciplineDTO dto) {
        if (dto == null) {
            throw new BadRequestException("Dados da disciplina são obrigatórios");
        }
        if (dto.name() == null || dto.name().isBlank()) {
            throw new BadRequestException("Nome da disciplina é obrigatório");
        }
        if (dto.code() == null || dto.code().isBlank()) {
            throw new BadRequestException("Código da disciplina é obrigatório");
        }
    }

    private void validateExtractedDTO(ExtractedDisciplineDTO dto) {
        if (dto == null) {
            throw new BadRequestException("Dados da disciplina são obrigatórios");
        }
        if (dto.name() == null || dto.name().isBlank()) {
            throw new BadRequestException("Nome da disciplina é obrigatório");
        }
        if (dto.code() == null || dto.code().isBlank()) {
            throw new BadRequestException("Código da disciplina é obrigatório");
        }
        if (dto.scheduleCode() == null || dto.scheduleCode().isBlank()) {
            throw new BadRequestException("Código de horário é obrigatório para inferir carga horária");
        }
    }
}


