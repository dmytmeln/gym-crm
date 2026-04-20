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
import static com.gym.crm.factory.TrainerTestFactory.DEFAULT_USERNAME;
import static com.gym.crm.factory.TrainerTestFactory.trainer;
import static com.gym.crm.factory.TrainerTestFactory.DEFAULT_TRAINER_ID;
import static com.gym.crm.factory.TrainerTestFactory.SECONDARY_TRAINER_ID;
import static com.gym.crm.factory.TrainerTestFactory.trainerCreateDto;
import static com.gym.crm.factory.TrainerTestFactory.trainerUpdateDto;
import static com.gym.crm.factory.TrainerTestFactory.trainerWithId;
import static com.gym.crm.factory.TrainingTypeTestFactory.STRENGTH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TrainerMapperTest {

    private TrainerMapper mapper;

    @BeforeEach
    public void setUp() {
        mapper = Mappers.getMapper(TrainerMapper.class);
    }

    @Test
    public void shouldMapAllFieldsCorrectlyWhenTrainerIsValid() {
        Trainer trainer = trainerWithId(DEFAULT_TRAINER_ID);

        TrainerResponseDto result = mapper.toDto(trainer);

        assertNotNull(result);
        assertEquals(DEFAULT_TRAINER_ID, result.userId());
        assertEquals(DEFAULT_USERNAME, result.username());
        assertEquals(DEFAULT_FIRST_NAME, result.firstName());
        assertEquals(DEFAULT_LAST_NAME, result.lastName());
        assertTrue(result.active());
        assertEquals(DEFAULT_SPECIALIZATION, result.specialization().getTrainingTypeName());
    }

    @Test
    public void shouldReturnNullWhenTrainerIsNull() {
        TrainerResponseDto result = mapper.toDto(null);

        assertNull(result);
    }

    @Test
    public void shouldMapAllFieldsCorrectlyWhenMappingToCreateResponseDto() {
        Trainer trainer = trainerWithId(DEFAULT_TRAINER_ID);

        TrainerCreateResponseDto result = mapper.toCreateResponseDto(trainer);

        assertNotNull(result);
        assertEquals(DEFAULT_TRAINER_ID, result.userId());
        assertEquals(DEFAULT_USERNAME, result.username());
        assertEquals(DEFAULT_FIRST_NAME, result.firstName());
        assertEquals(DEFAULT_LAST_NAME, result.lastName());
        assertEquals(DEFAULT_PASSWORD, result.password());
        assertTrue(result.active());
        assertEquals(DEFAULT_SPECIALIZATION, result.specialization().getTrainingTypeName());
    }

    @Test
    public void shouldReturnNullWhenMappingNullTrainerToCreateResponseDto() {
        TrainerCreateResponseDto result = mapper.toCreateResponseDto(null);

        assertNull(result);
    }

    @Test
    public void shouldMapAllFieldsAndIgnoreUserIdUsernamePasswordWhenMappingFromCreateDto() {
        TrainerCreateDto dto = trainerCreateDto();

        Trainer result = mapper.toEntity(dto);

        assertNotNull(result);
        assertNull(result.getUserId());
        assertNull(result.getUsername());
        assertNull(result.getPassword());
        assertEquals(DEFAULT_FIRST_NAME, result.getFirstName());
        assertEquals(DEFAULT_LAST_NAME, result.getLastName());
        assertTrue(result.isActive());
        assertEquals(DEFAULT_SPECIALIZATION, result.getSpecialization().getTrainingTypeName());
    }

    @Test
    public void shouldReturnNullWhenCreateDtoIsNull() {
        Trainer result = mapper.toEntity((TrainerCreateDto) null);

        assertNull(result);
    }

    @Test
    public void shouldMapAllFieldsCorrectlyWhenMappingFromUpdateDtoWithUserId() {
        TrainerUpdateDto dto = trainerUpdateDto();

        Trainer result = mapper.toEntity(dto, DEFAULT_TRAINER_ID);

        assertNotNull(result);
        assertEquals(DEFAULT_TRAINER_ID, result.getUserId());
        assertNull(result.getUsername());
        assertFalse(result.isActive());
        assertEquals(STRENGTH, result.getSpecialization().getTrainingTypeName());
    }

    @Test
    public void shouldCreateEntityWithOnlyUserIdWhenUpdateDtoIsNullButUserIdProvided() {
        Trainer result = mapper.toEntity(null, DEFAULT_TRAINER_ID);

        assertNotNull(result);
        assertEquals(DEFAULT_TRAINER_ID, result.getUserId());
        assertNull(result.getUsername());
        assertNull(result.getFirstName());
        assertNull(result.getLastName());
        assertNull(result.getPassword());
        assertFalse(result.isActive());
        assertNull(result.getSpecialization());
    }

    @Test
    public void shouldReturnNullWhenBothUpdateDtoAndUserIdAreNull() {
        Trainer result = mapper.toEntity(null, null);

        assertNull(result);
    }

    @Test
    public void shouldMapFieldsAndLeaveUserIdNullWhenUpdateDtoIsValidButUserIdIsNull() {
        TrainerUpdateDto dto = trainerUpdateDto();

        Trainer result = mapper.toEntity(dto, null);

        assertNotNull(result);
        assertNull(result.getUserId());
        assertNull(result.getUsername());
        assertFalse(result.isActive());
        assertEquals(STRENGTH, result.getSpecialization().getTrainingTypeName());
    }

    @Test
    public void shouldMapAllItemsWhenTrainerListIsValid() {
        Trainer trainer1 = trainerWithId(DEFAULT_TRAINER_ID);
        Trainer trainer2 = trainer(SECONDARY_TRAINER_ID, "jane.smith");
        List<Trainer> trainers = List.of(trainer1, trainer2);

        List<TrainerResponseDto> result = mapper.toDtoList(trainers);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(DEFAULT_TRAINER_ID, result.get(0).userId());
        assertEquals(DEFAULT_USERNAME, result.get(0).username());
        assertEquals(SECONDARY_TRAINER_ID, result.get(1).userId());
        assertEquals("jane.smith", result.get(1).username());
    }

    @Test
    public void shouldReturnEmptyListWhenTrainerListIsEmpty() {
        List<Trainer> emptyList = Collections.emptyList();

        List<TrainerResponseDto> result = mapper.toDtoList(emptyList);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldReturnNullWhenTrainerListIsNull() {
        List<TrainerResponseDto> result = mapper.toDtoList(null);

        assertNull(result);
    }

}
