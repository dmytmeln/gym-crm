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
import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_USER_ID;
import static com.gym.crm.factory.TraineeTestFactory.SECONDARY_TRAINEE_ID;
import static com.gym.crm.factory.TraineeTestFactory.buildTrainee;
import static com.gym.crm.factory.TraineeTestFactory.buildTraineeCreateDto;
import static com.gym.crm.factory.TraineeTestFactory.buildTraineeUpdateDto;
import static com.gym.crm.factory.TraineeTestFactory.buildTraineeWithId;
import static com.gym.crm.factory.TraineeTestFactory.buildTraineeWithIdAndUserId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TraineeMapperTest {

    private TraineeMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(TraineeMapper.class);
    }

    @Test
    void shouldMapAllFieldsCorrectlyWhenTraineeIsValid() {
        Trainee trainee = buildTraineeWithIdAndUserId();

        TraineeResponseDto result = mapper.toDto(trainee);

        assertNotNull(result);
        assertEquals(DEFAULT_TRAINEE_ID, result.id());
        assertEquals(DEFAULT_USER_ID, result.userId());
        assertEquals(DEFAULT_USERNAME, result.username());
        assertEquals(DEFAULT_FIRST_NAME, result.firstName());
        assertEquals(DEFAULT_LAST_NAME, result.lastName());
        assertTrue(result.active());
        assertEquals(DEFAULT_ADDRESS, result.address());
        assertEquals(DEFAULT_DATE_OF_BIRTH, result.dateOfBirth());
    }

    @Test
    void shouldReturnNullWhenTraineeIsNull() {
        TraineeResponseDto result = mapper.toDto(null);

        assertNull(result);
    }

    @Test
    void shouldMapAllFieldsCorrectlyWhenMappingToCreateResponseDto() {
        Trainee trainee = buildTraineeWithIdAndUserId();

        TraineeCreateResponseDto result = mapper.toCreateResponseDto(trainee);

        assertNotNull(result);
        assertEquals(DEFAULT_TRAINEE_ID, result.id());
        assertEquals(DEFAULT_USER_ID, result.userId());
        assertEquals(DEFAULT_USERNAME, result.username());
        assertEquals(DEFAULT_FIRST_NAME, result.firstName());
        assertEquals(DEFAULT_LAST_NAME, result.lastName());
        assertEquals(DEFAULT_PASSWORD, result.password());
        assertTrue(result.active());
        assertEquals(DEFAULT_ADDRESS, result.address());
        assertEquals(DEFAULT_DATE_OF_BIRTH, result.dateOfBirth());
    }

    @Test
    void shouldReturnNullWhenMappingNullTraineeToCreateResponseDto() {
        TraineeCreateResponseDto result = mapper.toCreateResponseDto(null);

        assertNull(result);
    }

    @Test
    void shouldMapAllFieldsAndIgnoreUserIdUsernamePasswordWhenMappingFromCreateDto() {
        TraineeCreateDto dto = buildTraineeCreateDto();

        Trainee result = mapper.toEntity(dto);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getUser().getId());
        assertNull(result.getUser().getUsername());
        assertNull(result.getUser().getPassword());
        assertEquals(DEFAULT_FIRST_NAME, result.getUser().getFirstName());
        assertEquals(DEFAULT_LAST_NAME, result.getUser().getLastName());
        assertTrue(result.getUser().getIsActive());
        assertEquals(DEFAULT_ADDRESS, result.getAddress());
        assertEquals(DEFAULT_DATE_OF_BIRTH, result.getDateOfBirth());
    }

    @Test
    void shouldReturnNullWhenCreateDtoIsNull() {
        Trainee result = mapper.toEntity(null);

        assertNull(result);
    }

    @Test
    void shouldMapAllFieldsCorrectlyWhenMappingFromUpdateDtoWithUserId() {
        TraineeUpdateDto dto = buildTraineeUpdateDto();

        Trainee result = mapper.toEntity(dto, DEFAULT_TRAINEE_ID);

        assertNotNull(result);
        assertNull(result.getUser().getUsername());
        assertNull(result.getUser().getPassword());
        assertNull(result.getUser().getId());
        assertFalse(result.getUser().getIsActive());
        assertEquals(DEFAULT_TRAINEE_ID, result.getId());
        assertEquals(dto.firstName(), result.getUser().getFirstName());
        assertEquals(dto.lastName(), result.getUser().getLastName());
        assertEquals(dto.address(), result.getAddress());
        assertEquals(dto.dateOfBirth(), result.getDateOfBirth());
    }

    @Test
    void shouldCreateEntityWithOnlyUserIdWhenUpdateDtoIsNullButUserIdProvided() {
        Trainee result = mapper.toEntity(null, DEFAULT_TRAINEE_ID);

        assertNotNull(result);
        assertEquals(DEFAULT_TRAINEE_ID, result.getId());
        assertNull(result.getUser());
        assertNull(result.getAddress());
        assertNull(result.getDateOfBirth());
    }

    @Test
    void shouldReturnNullWhenBothUpdateDtoAndUserIdAreNull() {
        Trainee result = mapper.toEntity(null, null);

        assertNull(result);
    }

    @Test
    void shouldMapFieldsAndLeaveUserIdNullWhenUpdateDtoIsValidButUserIdIsNull() {
        TraineeUpdateDto dto = buildTraineeUpdateDto();

        Trainee result = mapper.toEntity(dto, null);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getUser().getUsername());
        assertFalse(result.getUser().getIsActive());
    }

    @Test
    void shouldMapAllItemsWhenTraineeListIsValid() {
        Trainee trainee1 = buildTraineeWithId(DEFAULT_TRAINEE_ID);
        Trainee trainee2 = buildTrainee(SECONDARY_TRAINEE_ID, "sophia.wilson");
        List<Trainee> trainees = List.of(trainee1, trainee2);

        List<TraineeResponseDto> result = mapper.toDtoList(trainees);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(DEFAULT_TRAINEE_ID, result.get(0).id());
        assertEquals(DEFAULT_USERNAME, result.get(0).username());
        assertEquals(SECONDARY_TRAINEE_ID, result.get(1).id());
        assertEquals("sophia.wilson", result.get(1).username());
    }

    @Test
    void shouldReturnEmptyListWhenTraineeListIsEmpty() {
        List<Trainee> emptyList = Collections.emptyList();

        List<TraineeResponseDto> result = mapper.toDtoList(emptyList);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnNullWhenTraineeListIsNull() {
        List<TraineeResponseDto> result = mapper.toDtoList(null);

        assertNull(result);
    }

}
