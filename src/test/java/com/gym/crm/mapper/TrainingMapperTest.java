package com.gym.crm.mapper;

import com.gym.crm.dto.TrainingCreateDto;
import com.gym.crm.dto.TrainingResponseDto;
import com.gym.crm.model.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;

import static com.gym.crm.factory.TrainingTestFactory.DEFAULT_DATE;
import static com.gym.crm.factory.TrainingTestFactory.DEFAULT_DURATION;
import static com.gym.crm.factory.TrainingTestFactory.DEFAULT_TRAINEE_ID;
import static com.gym.crm.factory.TrainingTestFactory.DEFAULT_TRAINER_ID;
import static com.gym.crm.factory.TrainingTestFactory.DEFAULT_TRAINING_ID;
import static com.gym.crm.factory.TrainingTestFactory.DEFAULT_TRAINING_NAME;
import static com.gym.crm.factory.TrainingTestFactory.DEFAULT_TRAINING_TYPE;
import static com.gym.crm.factory.TrainingTestFactory.SECONDARY_TRAINING_ID;
import static com.gym.crm.factory.TrainingTestFactory.buildTrainingCreateDto;
import static com.gym.crm.factory.TrainingTestFactory.buildTrainingWithId;
import static com.gym.crm.factory.TrainingTestFactory.buildTrainingWithIdAndType;
import static com.gym.crm.factory.TrainingTypeTestFactory.STRENGTH;
import static com.gym.crm.factory.TrainingTypeTestFactory.buildStrength;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrainingMapperTest {

    private TrainingMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(TrainingMapper.class);
    }

    @Test
    void shouldMapAllFieldsCorrectlyWhenTrainingIsValid() {
        Training training = buildTrainingWithId(DEFAULT_TRAINING_ID);

        TrainingResponseDto result = mapper.toDto(training);

        assertNotNull(result);
        assertEquals(DEFAULT_TRAINING_ID, result.id());
        assertEquals(DEFAULT_TRAINEE_ID, result.traineeId());
        assertEquals(DEFAULT_TRAINER_ID, result.trainerId());
        assertEquals(DEFAULT_TRAINING_NAME, result.trainingName());
        assertEquals(DEFAULT_TRAINING_TYPE, result.trainingType().getTrainingTypeName());
        assertEquals(DEFAULT_DURATION, result.trainingDuration());
        assertEquals(DEFAULT_DATE, result.trainingDate());
    }

    @Test
    void shouldReturnNullWhenTrainingIsNull() {
        TrainingResponseDto result = mapper.toDto(null);

        assertNull(result);
    }

    @Test
    void shouldMapAllFieldsAndIgnoreIdWhenMappingFromCreateDto() {
        TrainingCreateDto dto = buildTrainingCreateDto();

        Training result = mapper.toEntity(dto);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals(DEFAULT_TRAINEE_ID, result.getTraineeId());
        assertEquals(DEFAULT_TRAINER_ID, result.getTrainerId());
        assertEquals(DEFAULT_TRAINING_TYPE, result.getTrainingType().getTrainingTypeName());
        assertEquals(DEFAULT_DURATION, result.getTrainingDuration());
    }

    @Test
    void shouldReturnNullWhenCreateDtoIsNull() {
        Training result = mapper.toEntity(null);

        assertNull(result);
    }

    @Test
    void shouldMapAllItemsWhenTrainingListIsValid() {
        Training training1 = buildTrainingWithId(DEFAULT_TRAINING_ID);
        Training training2 = buildTrainingWithIdAndType(SECONDARY_TRAINING_ID, buildStrength());
        List<Training> trainings = List.of(training1, training2);

        List<TrainingResponseDto> result = mapper.toDtoList(trainings);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(DEFAULT_TRAINING_ID, result.get(0).id());
        assertEquals(DEFAULT_TRAINING_NAME, result.get(0).trainingName());
        assertEquals(SECONDARY_TRAINING_ID, result.get(1).id());
        assertEquals(STRENGTH, result.get(1).trainingType().getTrainingTypeName());
    }

    @Test
    void shouldReturnEmptyListWhenTrainingListIsEmpty() {
        List<Training> emptyList = Collections.emptyList();

        List<TrainingResponseDto> result = mapper.toDtoList(emptyList);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnNullWhenTrainingListIsNull() {
        List<TrainingResponseDto> result = mapper.toDtoList(null);

        assertNull(result);
    }

}
