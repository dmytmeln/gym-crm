package com.gym.crm.mapper;

import com.gym.crm.dto.TraineeCreateDto;
import com.gym.crm.dto.TraineeCreateResponseDto;
import com.gym.crm.dto.TraineeResponseDto;
import com.gym.crm.dto.TraineeUpdateDto;
import com.gym.crm.entity.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;

import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_ADDRESS;
import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_DATE_OF_BIRTH;
import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_FIRST_NAME;
import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_LAST_NAME;
import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_PASSWORD;
import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_TRAINEE_ID;
import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_USERNAME;
import static com.gym.crm.factory.TraineeTestFactory.SECONDARY_TRAINEE_ID;
import static com.gym.crm.factory.TraineeTestFactory.trainee;
import static com.gym.crm.factory.TraineeTestFactory.traineeCreateDto;
import static com.gym.crm.factory.TraineeTestFactory.traineeUpdateDto;
import static com.gym.crm.factory.TraineeTestFactory.traineeWithId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TraineeMapperTest {

    private TraineeMapper mapper;

    @BeforeEach
    public void setUp() {
        mapper = Mappers.getMapper(TraineeMapper.class);
    }

    @Test
    public void shouldMapAllFieldsCorrectlyWhenTraineeIsValid() {
        Trainee trainee = traineeWithId(DEFAULT_TRAINEE_ID);

        TraineeResponseDto result = mapper.toDto(trainee);

        assertNotNull(result);
        assertEquals(DEFAULT_TRAINEE_ID, result.userId());
        assertEquals(DEFAULT_USERNAME, result.username());
        assertEquals(DEFAULT_FIRST_NAME, result.firstName());
        assertEquals(DEFAULT_LAST_NAME, result.lastName());
        assertTrue(result.active());
        assertEquals(DEFAULT_ADDRESS, result.address());
        assertEquals(DEFAULT_DATE_OF_BIRTH, result.dateOfBirth());
    }

    @Test
    public void shouldReturnNullWhenTraineeIsNull() {
        TraineeResponseDto result = mapper.toDto(null);

        assertNull(result);
    }

    @Test
    public void shouldMapAllFieldsCorrectlyWhenMappingToCreateResponseDto() {
        Trainee trainee = traineeWithId(DEFAULT_TRAINEE_ID);

        TraineeCreateResponseDto result = mapper.toCreateResponseDto(trainee);

        assertNotNull(result);
        assertEquals(DEFAULT_TRAINEE_ID, result.userId());
        assertEquals(DEFAULT_USERNAME, result.username());
        assertEquals(DEFAULT_FIRST_NAME, result.firstName());
        assertEquals(DEFAULT_LAST_NAME, result.lastName());
        assertEquals(DEFAULT_PASSWORD, result.password());
        assertTrue(result.active());
        assertEquals(DEFAULT_ADDRESS, result.address());
        assertEquals(DEFAULT_DATE_OF_BIRTH, result.dateOfBirth());
    }

    @Test
    public void shouldReturnNullWhenMappingNullTraineeToCreateResponseDto() {
        TraineeCreateResponseDto result = mapper.toCreateResponseDto(null);

        assertNull(result);
    }

    @Test
    public void shouldMapAllFieldsAndIgnoreUserIdUsernamePasswordWhenMappingFromCreateDto() {
        TraineeCreateDto dto = traineeCreateDto();

        Trainee result = mapper.toEntity(dto);

        assertNotNull(result);
        assertNull(result.getUserId());
        assertNull(result.getUsername());
        assertNull(result.getPassword());
        assertEquals(DEFAULT_FIRST_NAME, result.getFirstName());
        assertEquals(DEFAULT_LAST_NAME, result.getLastName());
        assertTrue(result.isActive());
        assertEquals(DEFAULT_ADDRESS, result.getAddress());
        assertEquals(DEFAULT_DATE_OF_BIRTH, result.getDateOfBirth());
    }

    @Test
    public void shouldReturnNullWhenCreateDtoIsNull() {
        Trainee result = mapper.toEntity(null);

        assertNull(result);
    }

    @Test
    public void shouldMapAllFieldsCorrectlyWhenMappingFromUpdateDtoWithUserId() {
        TraineeUpdateDto dto = traineeUpdateDto();

        Trainee result = mapper.toEntity(dto, DEFAULT_TRAINEE_ID);

        assertNotNull(result);
        assertEquals(DEFAULT_TRAINEE_ID, result.getUserId());
        assertNull(result.getUsername());
        assertFalse(result.isActive());
    }

    @Test
    public void shouldCreateEntityWithOnlyUserIdWhenUpdateDtoIsNullButUserIdProvided() {
        Trainee result = mapper.toEntity(null, DEFAULT_TRAINEE_ID);

        assertNotNull(result);
        assertEquals(DEFAULT_TRAINEE_ID, result.getUserId());
        assertNull(result.getUsername());
        assertNull(result.getFirstName());
        assertNull(result.getLastName());
        assertNull(result.getPassword());
        assertFalse(result.isActive());
        assertNull(result.getAddress());
        assertNull(result.getDateOfBirth());
    }

    @Test
    public void shouldReturnNullWhenBothUpdateDtoAndUserIdAreNull() {
        Trainee result = mapper.toEntity(null, null);

        assertNull(result);
    }

    @Test
    public void shouldMapFieldsAndLeaveUserIdNullWhenUpdateDtoIsValidButUserIdIsNull() {
        TraineeUpdateDto dto = traineeUpdateDto();

        Trainee result = mapper.toEntity(dto, null);

        assertNotNull(result);
        assertNull(result.getUserId());
        assertNull(result.getUsername());
        assertFalse(result.isActive());
    }

    @Test
    public void shouldMapAllItemsWhenTraineeListIsValid() {
        Trainee trainee1 = traineeWithId(DEFAULT_TRAINEE_ID);
        Trainee trainee2 = trainee(SECONDARY_TRAINEE_ID, "jane.smith");
        List<Trainee> trainees = List.of(trainee1, trainee2);

        List<TraineeResponseDto> result = mapper.toDtoList(trainees);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(DEFAULT_TRAINEE_ID, result.get(0).userId());
        assertEquals(DEFAULT_USERNAME, result.get(0).username());
        assertEquals(SECONDARY_TRAINEE_ID, result.get(1).userId());
        assertEquals("jane.smith", result.get(1).username());
    }

    @Test
    public void shouldReturnEmptyListWhenTraineeListIsEmpty() {
        List<Trainee> emptyList = Collections.emptyList();

        List<TraineeResponseDto> result = mapper.toDtoList(emptyList);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldReturnNullWhenTraineeListIsNull() {
        List<TraineeResponseDto> result = mapper.toDtoList(null);

        assertNull(result);
    }

}
