package com.gym.crm.mapper;

import com.gym.crm.dto.TrainerCreateDto;
import com.gym.crm.dto.TrainerCreateResponseDto;
import com.gym.crm.dto.TrainerResponseDto;
import com.gym.crm.dto.TrainerUpdateDto;
import com.gym.crm.entity.Trainer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface TrainerMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "active", source = "user.isActive")
    @Mapping(target = "specializationName", source = "specialization.trainingTypeName")
    TrainerResponseDto toDto(Trainer trainer);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "password", source = "user.password")
    @Mapping(target = "active", source = "user.isActive")
    @Mapping(target = "specializationName", source = "specialization.trainingTypeName")
    TrainerCreateResponseDto toCreateResponseDto(Trainer trainer);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "trainees", ignore = true)
    @Mapping(target = "user.firstName", source = "firstName")
    @Mapping(target = "user.lastName", source = "lastName")
    @Mapping(target = "user.isActive", source = "active")
    @Mapping(target = "specialization.id", source = "specializationId")
    Trainer toEntity(TrainerCreateDto dto);

    List<TrainerResponseDto> toDtoList(List<Trainer> trainers);

    @Mapping(target = "id", source = "trainerId")
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "trainees", ignore = true)
    @Mapping(target = "user.firstName", source = "dto.firstName")
    @Mapping(target = "user.lastName", source = "dto.lastName")
    @Mapping(target = "user.isActive", source = "dto.active")
    @Mapping(target = "specialization.id", source = "dto.specializationId")
    Trainer toEntity(TrainerUpdateDto dto, Long trainerId);

}
