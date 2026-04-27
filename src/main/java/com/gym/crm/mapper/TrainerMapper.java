package com.gym.crm.mapper;

import com.gym.crm.dto.TrainerCreateDto;
import com.gym.crm.dto.TrainerCreateResponseDto;
import com.gym.crm.dto.TrainerResponseDto;
import com.gym.crm.dto.TrainerUpdateDto;
import com.gym.crm.model.Trainer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface TrainerMapper {

    TrainerResponseDto toDto(Trainer trainer);

    TrainerCreateResponseDto toCreateResponseDto(Trainer trainer);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "isActive", source = "active")
    Trainer toEntity(TrainerCreateDto dto);

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "isActive", source = "dto.active")
    Trainer toEntity(TrainerUpdateDto dto, Long userId);

    List<TrainerResponseDto> toDtoList(List<Trainer> trainers);

}
