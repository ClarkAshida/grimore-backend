package com.grimore.mapper;

import com.grimore.dto.request.CreateTaskDTO;
import com.grimore.dto.response.TaskDTO;
import com.grimore.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(source = "discipline.id", target = "disciplineId")
    @Mapping(source = "discipline.name", target = "disciplineName")
    TaskDTO toDTO(Task task);

    List<TaskDTO> toDTO(List<Task> tasks);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "discipline", ignore = true)
    @Mapping(target = "completed", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Task toEntity(CreateTaskDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "discipline", ignore = true)
    @Mapping(target = "completed", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(CreateTaskDTO dto, @MappingTarget Task task);
}

