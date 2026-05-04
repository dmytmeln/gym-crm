package com.gym.crm.mapper;

import com.gym.crm.dto.TraineeCreateDto;
import com.gym.crm.dto.TraineeCreateResponseDto;
import com.gym.crm.dto.TraineeResponseDto;
import com.gym.crm.dto.TraineeUpdateDto;
import com.gym.crm.entity.Trainee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface TraineeMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "active", source = "user.isActive")
    TraineeResponseDto toDto(Trainee trainee);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "password", source = "user.password")
    @Mapping(target = "active", source = "user.isActive")
    TraineeCreateResponseDto toCreateResponseDto(Trainee trainee);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "trainers", ignore = true)
    @Mapping(target = "user.firstName", source = "firstName")
    @Mapping(target = "user.lastName", source = "lastName")
    @Mapping(target = "user.isActive", source = "active")
    Trainee toEntity(TraineeCreateDto dto);

    @Mapping(target = "id", source = "traineeId")
    @Mapping(target = "user.firstName", source = "dto.firstName")
    @Mapping(target = "user.lastName", source = "dto.lastName")
    @Mapping(target = "user.isActive", source = "dto.active")
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "trainers", ignore = true)
    Trainee toEntity(TraineeUpdateDto dto, Long traineeId);

    List<TraineeResponseDto> toDtoList(List<Trainee> trainees);

}
