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

    TraineeResponseDto toDto(Trainee trainee);

    TraineeCreateResponseDto toCreateResponseDto(Trainee trainee);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "isActive", source = "active")
    Trainee toEntity(TraineeCreateDto dto);

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "isActive", source = "dto.active")
    Trainee toEntity(TraineeUpdateDto dto, Long userId);

    List<TraineeResponseDto> toDtoList(List<Trainee> trainees);

}
