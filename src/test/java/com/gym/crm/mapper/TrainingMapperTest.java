package com.gym.crm.mapper;

import com.gym.crm.dto.TrainingCreateDto;
import com.gym.crm.dto.TrainingResponseDto;
import com.gym.crm.entity.Training;
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
import static com.gym.crm.factory.TrainingTestFactory.DEFAULT_TRAINING_TYPE_ID;
import static com.gym.crm.factory.TrainingTestFactory.DEFAULT_TRAINING_TYPE_NAME;
import static com.gym.crm.factory.TrainingTestFactory.SECONDARY_TRAINING_ID;
import static com.gym.crm.factory.TrainingTestFactory.buildTrainingCreateDto;
import static com.gym.crm.factory.TrainingTestFactory.buildTrainingWithId;
import static com.gym.crm.factory.TrainingTestFactory.buildTrainingWithIdAndTypeName;
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
        assertEquals(DEFAULT_TRAINING_TYPE_NAME, result.trainingTypeName());
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
        assertEquals(DEFAULT_TRAINEE_ID, result.getTrainee().getId());
        assertEquals(DEFAULT_TRAINER_ID, result.getTrainer().getId());
        assertEquals(DEFAULT_TRAINING_TYPE_ID, result.getTrainingType().getId());
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
        Training training2 = buildTrainingWithIdAndTypeName(SECONDARY_TRAINING_ID, "CARDIO");
        List<Training> trainings = List.of(training1, training2);

        List<TrainingResponseDto> result = mapper.toDtoList(trainings);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(DEFAULT_TRAINING_ID, result.get(0).id());
        assertEquals(DEFAULT_TRAINING_NAME, result.get(0).trainingName());
        assertEquals(DEFAULT_TRAINING_TYPE_NAME, result.get(0).trainingTypeName());
        assertEquals(SECONDARY_TRAINING_ID, result.get(1).id());
        assertEquals(training2.getTrainingType().getTrainingTypeName(), result.get(1).trainingTypeName());
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
