package com.gym.crm.mapper;

import com.gia.openapi.model.LoginChangeRequest;
import com.gia.openapi.model.LoginRequest;
import com.gym.crm.dto.LoginChangeDto;
import com.gym.crm.dto.LoginRequestDto;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface AuthMapper {

    LoginRequestDto toDto(LoginRequest loginRequest);

    LoginChangeDto toDto(LoginChangeRequest loginChangeRequest);

}
