package com.grimore.service;

                import com.grimore.dto.request.CreateDisciplineDTO;
                import com.grimore.dto.response.DisciplineDTO;
                import com.grimore.mapper.DisciplineMapper;
                import com.grimore.model.Discipline;
                import com.grimore.model.Student;
                import com.grimore.repository.DisciplineRepository;
                import com.grimore.repository.StudentRepository;
                import jakarta.persistence.EntityNotFoundException;
                import lombok.RequiredArgsConstructor;
                import org.springframework.stereotype.Service;
                import org.springframework.transaction.annotation.Transactional;

                import java.util.List;
                import java.util.UUID;

                @Service
                @RequiredArgsConstructor
                public class DisciplineService {
                    private final DisciplineRepository disciplineRepository;
                    private final StudentRepository studentRepository;
                    private final DisciplineMapper mapper;

                    @Transactional
                    public DisciplineDTO create(CreateDisciplineDTO dto) {
                        Student student = findStudentById(dto.studentId());
                        validateDuplicateCode(dto.studentId(), dto.code());

                        Discipline discipline = mapper.toEntity(dto);
                        discipline.setStudent(student);

                        return mapper.toDTO(disciplineRepository.save(discipline));
                    }

                    @Transactional(readOnly = true)
                    public DisciplineDTO findById(UUID id) {
                        Discipline discipline = findDisciplineById(id);
                        return mapper.toDTO(discipline);
                    }

                    @Transactional(readOnly = true)
                    public List<DisciplineDTO> findByStudentId(UUID studentId, boolean activeOnly) {
                        List<Discipline> disciplines = activeOnly
                                ? disciplineRepository.findByStudentIdAndActiveTrue(studentId)
                                : disciplineRepository.findByStudentId(studentId);
                        return mapper.toDTO(disciplines);
                    }

                    @Transactional
                    public DisciplineDTO update(UUID id, CreateDisciplineDTO dto) {
                        Discipline discipline = findDisciplineById(id);

                        if (isCodeChanged(discipline, dto.code())) {
                            validateDuplicateCode(dto.studentId(), dto.code());
                        }

                        mapper.updateEntity(dto, discipline);
                        return mapper.toDTO(disciplineRepository.save(discipline));
                    }

                    @Transactional
                    public void deactivate(UUID id) {
                        Discipline discipline = findDisciplineById(id);
                        discipline.setActive(false);
                        disciplineRepository.save(discipline);
                    }

                    @Transactional
                    public void delete(UUID id) {
                        if (!disciplineRepository.existsById(id)) {
                            throw new EntityNotFoundException("Disciplina não encontrada");
                        }
                        disciplineRepository.deleteById(id);
                    }

                    private Discipline findDisciplineById(UUID id) {
                        return disciplineRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException("Disciplina não encontrada"));
                    }

                    private Student findStudentById(UUID studentId) {
                        return studentRepository.findById(studentId)
                                .orElseThrow(() -> new EntityNotFoundException("Estudante não encontrado"));
                    }

                    private void validateDuplicateCode(UUID studentId, String code) {
                        if (code != null && disciplineRepository.existsByStudentIdAndCodeAndActiveTrue(studentId, code)) {
                            throw new IllegalArgumentException("Já existe uma disciplina ativa com este código para o estudante");
                        }
                    }

                    private boolean isCodeChanged(Discipline discipline, String newCode) {
                        return newCode != null && !newCode.equals(discipline.getCode());
                    }
                }