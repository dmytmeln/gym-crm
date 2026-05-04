package com.gym.crm.mapper;

import com.gym.crm.dto.TrainerCreateDto;
import com.gym.crm.dto.TrainerCreateResponseDto;
import com.gym.crm.dto.TrainerResponseDto;
import com.gym.crm.dto.TrainerUpdateDto;
import com.gym.crm.entity.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;

import static com.gym.crm.factory.TrainerTestFactory.DEFAULT_FIRST_NAME;
import static com.gym.crm.factory.TrainerTestFactory.DEFAULT_LAST_NAME;
import static com.gym.crm.factory.TrainerTestFactory.DEFAULT_PASSWORD;
import static com.gym.crm.factory.TrainerTestFactory.DEFAULT_SPECIALIZATION;
import static com.gym.crm.factory.TrainerTestFactory.DEFAULT_SPECIALIZATION_ID;
import static com.gym.crm.factory.TrainerTestFactory.DEFAULT_TRAINER_ID;
import static com.gym.crm.factory.TrainerTestFactory.DEFAULT_USERNAME;
import static com.gym.crm.factory.TrainerTestFactory.DEFAULT_USER_ID;
import static com.gym.crm.factory.TrainerTestFactory.SECONDARY_TRAINER_ID;
import static com.gym.crm.factory.TrainerTestFactory.buildTrainer;
import static com.gym.crm.factory.TrainerTestFactory.buildTrainerCreateDto;
import static com.gym.crm.factory.TrainerTestFactory.buildTrainerUpdateDto;
import static com.gym.crm.factory.TrainerTestFactory.buildTrainerWithId;
import static com.gym.crm.factory.TrainerTestFactory.buildTrainerWithIdAndUserId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrainerMapperTest {

    private TrainerMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(TrainerMapper.class);
    }

    @Test
    void shouldMapAllFieldsCorrectlyWhenTrainerIsValid() {
        Trainer trainer = buildTrainerWithIdAndUserId();

        TrainerResponseDto result = mapper.toDto(trainer);

        assertNotNull(result);
        assertEquals(DEFAULT_TRAINER_ID, result.id());
        assertEquals(DEFAULT_USER_ID, result.userId());
        assertEquals(DEFAULT_USERNAME, result.username());
        assertEquals(DEFAULT_FIRST_NAME, result.firstName());
        assertEquals(DEFAULT_LAST_NAME, result.lastName());
        assertTrue(result.active());
        assertEquals(DEFAULT_SPECIALIZATION, result.specializationName());
    }

    @Test
    void shouldReturnNullWhenTrainerIsNull() {
        TrainerResponseDto result = mapper.toDto(null);

        assertNull(result);
    }

    @Test
    void shouldMapAllFieldsCorrectlyWhenMappingToCreateResponseDto() {
        Trainer trainer = buildTrainerWithIdAndUserId();

        TrainerCreateResponseDto result = mapper.toCreateResponseDto(trainer);

        assertNotNull(result);
        assertEquals(DEFAULT_TRAINER_ID, result.id());
        assertEquals(DEFAULT_USER_ID, result.userId());
        assertEquals(DEFAULT_USERNAME, result.username());
        assertEquals(DEFAULT_FIRST_NAME, result.firstName());
        assertEquals(DEFAULT_LAST_NAME, result.lastName());
        assertEquals(DEFAULT_PASSWORD, result.password());
        assertTrue(result.active());
        assertEquals(DEFAULT_SPECIALIZATION, result.specializationName());
    }

    @Test
    void shouldReturnNullWhenMappingNullTrainerToCreateResponseDto() {
        TrainerCreateResponseDto result = mapper.toCreateResponseDto(null);

        assertNull(result);
    }

    @Test
    void shouldMapAllFieldsAndIgnoreUserIdUsernamePasswordWhenMappingFromCreateDto() {
        TrainerCreateDto dto = buildTrainerCreateDto();

        Trainer result = mapper.toEntity(dto);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getUser().getId());
        assertNull(result.getUser().getUsername());
        assertNull(result.getUser().getPassword());
        assertEquals(DEFAULT_FIRST_NAME, result.getUser().getFirstName());
        assertEquals(DEFAULT_LAST_NAME, result.getUser().getLastName());
        assertTrue(result.getUser().getIsActive());
        assertEquals(DEFAULT_SPECIALIZATION_ID, result.getSpecialization().getId());
    }

    @Test
    void shouldReturnNullWhenCreateDtoIsNull() {
        Trainer result = mapper.toEntity(null);

        assertNull(result);
    }

    @Test
    void shouldMapAllFieldsCorrectlyWhenMappingFromUpdateDtoWithUserId() {
        TrainerUpdateDto dto = buildTrainerUpdateDto();

        Trainer result = mapper.toEntity(dto, DEFAULT_TRAINER_ID);

        assertNotNull(result);
        assertNull(result.getUser().getId());
        assertNull(result.getUser().getPassword());
        assertNull(result.getUser().getUsername());
        assertFalse(result.getUser().getIsActive());
        assertEquals(DEFAULT_TRAINER_ID, result.getId());
        assertEquals(DEFAULT_SPECIALIZATION_ID, result.getSpecialization().getId());
    }

    @Test
    void shouldCreateEntityWithOnlyUserIdWhenUpdateDtoIsNullButUserIdProvided() {
        Trainer result = mapper.toEntity(null, DEFAULT_TRAINER_ID);

        assertNotNull(result);
        assertEquals(DEFAULT_TRAINER_ID, result.getId());
        assertNull(result.getUser());
        assertNull(result.getSpecialization());
    }

    @Test
    void shouldReturnNullWhenBothUpdateDtoAndUserIdAreNull() {
        Trainer result = mapper.toEntity(null, null);

        assertNull(result);
    }

    @Test
    void shouldMapFieldsAndLeaveUserIdNullWhenUpdateDtoIsValidButUserIdIsNull() {
        TrainerUpdateDto dto = buildTrainerUpdateDto();

        Trainer result = mapper.toEntity(dto, null);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getUser().getUsername());
        assertFalse(result.getUser().getIsActive());
        assertEquals(dto.specializationId(), result.getSpecialization().getId());
    }

    @Test
    void shouldMapAllItemsWhenTrainerListIsValid() {
        Trainer trainer1 = buildTrainerWithId(DEFAULT_TRAINER_ID);
        Trainer trainer2 = buildTrainer(SECONDARY_TRAINER_ID, "sarah.adams");
        List<Trainer> trainers = List.of(trainer1, trainer2);

        List<TrainerResponseDto> result = mapper.toDtoList(trainers);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(DEFAULT_TRAINER_ID, result.get(0).id());
        assertEquals(DEFAULT_USERNAME, result.get(0).username());
        assertEquals(SECONDARY_TRAINER_ID, result.get(1).id());
        assertEquals(trainer2.getUser().getUsername(), result.get(1).username());
    }

    @Test
    void shouldReturnEmptyListWhenTrainerListIsEmpty() {
        List<Trainer> emptyList = Collections.emptyList();

        List<TrainerResponseDto> result = mapper.toDtoList(emptyList);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnNullWhenTrainerListIsNull() {
        List<TrainerResponseDto> result = mapper.toDtoList(null);

        assertNull(result);
    }

}
