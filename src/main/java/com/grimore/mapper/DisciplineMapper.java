package com.grimore.mapper;

import com.grimore.dto.request.CreateDisciplineDTO;
import com.grimore.dto.response.DisciplineDTO;
import com.grimore.model.Discipline;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DisciplineMapper {

    @Mapping(source = "student.id", target = "studentId")
    DisciplineDTO toDTO(Discipline discipline);

    List<DisciplineDTO> toDTO(List<Discipline> disciplines);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "absencesHours", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Discipline toEntity(CreateDisciplineDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "absencesHours", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(CreateDisciplineDTO dto, @MappingTarget Discipline discipline);
}