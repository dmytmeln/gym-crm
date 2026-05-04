package com.gym.crm.mapper;

import com.gym.crm.dto.TrainingCreateDto;
import com.gym.crm.dto.TrainingResponseDto;
import com.gym.crm.entity.Training;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface TrainingMapper {

    @Mapping(target = "traineeId", source = "trainee.id")
    @Mapping(target = "trainerId", source = "trainer.id")
    @Mapping(target = "trainingTypeName", source = "trainingType.trainingTypeName")
    TrainingResponseDto toDto(Training training);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainee.id", source = "traineeId")
    @Mapping(target = "trainer.id", source = "trainerId")
    @Mapping(target = "trainingType.id", source = "trainingTypeId")
    Training toEntity(TrainingCreateDto dto);

    List<TrainingResponseDto> toDtoList(List<Training> trainings);

}
