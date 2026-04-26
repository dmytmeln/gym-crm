package com.gym.crm.storage.csv;

import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.storage.csv.dto.TraineeCsvDto;
import com.gym.crm.storage.csv.dto.TrainerCsvDto;
import com.gym.crm.storage.csv.dto.TrainingCsvDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static com.gym.crm.factory.CsvDtoTestFactory.DEFAULT_CSV_ADDRESS;
import static com.gym.crm.factory.CsvDtoTestFactory.DEFAULT_CSV_DATE_OF_BIRTH;
import static com.gym.crm.factory.CsvDtoTestFactory.DEFAULT_CSV_FIRST_NAME;
import static com.gym.crm.factory.CsvDtoTestFactory.DEFAULT_CSV_LAST_NAME;
import static com.gym.crm.factory.CsvDtoTestFactory.DEFAULT_CSV_PASSWORD;
import static com.gym.crm.factory.CsvDtoTestFactory.DEFAULT_CSV_SPECIALIZATION;
import static com.gym.crm.factory.CsvDtoTestFactory.DEFAULT_CSV_TRAINEE_ID;
import static com.gym.crm.factory.CsvDtoTestFactory.DEFAULT_CSV_TRAINER_ID;
import static com.gym.crm.factory.CsvDtoTestFactory.DEFAULT_CSV_TRAINING_DATE;
import static com.gym.crm.factory.CsvDtoTestFactory.DEFAULT_CSV_TRAINING_DURATION;
import static com.gym.crm.factory.CsvDtoTestFactory.DEFAULT_CSV_TRAINING_NAME;
import static com.gym.crm.factory.CsvDtoTestFactory.DEFAULT_CSV_TRAINING_TYPE;
import static com.gym.crm.factory.CsvDtoTestFactory.DEFAULT_CSV_USERNAME;
import static com.gym.crm.factory.CsvDtoTestFactory.buildTraineeCsvDto;
import static com.gym.crm.factory.CsvDtoTestFactory.buildTrainerCsvDto;
import static com.gym.crm.factory.CsvDtoTestFactory.buildTrainingCsvDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvEntityMapperTest {

    private CsvEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(CsvEntityMapper.class);
    }

    @Test
    void shouldMapTraineeCsvDtoToEntity() {
        TraineeCsvDto dto = buildTraineeCsvDto();

        Trainee result = mapper.toTrainee(dto);

        assertNotNull(result);
        assertNull(result.getUserId());
        assertEquals(DEFAULT_CSV_USERNAME, result.getUsername());
        assertEquals(DEFAULT_CSV_FIRST_NAME, result.getFirstName());
        assertEquals(DEFAULT_CSV_LAST_NAME, result.getLastName());
        assertEquals(DEFAULT_CSV_PASSWORD, result.getPassword());
        assertTrue(result.isActive());
        assertEquals(DEFAULT_CSV_ADDRESS, result.getAddress());
        assertEquals(DEFAULT_CSV_DATE_OF_BIRTH, result.getDateOfBirth());
    }

    @Test
    void shouldReturnNullWhenTraineeCsvDtoIsNull() {
        Trainee result = mapper.toTrainee(null);

        assertNull(result);
    }

    @Test
    void shouldMapTrainerCsvDtoToEntity() {
        TrainerCsvDto dto = buildTrainerCsvDto();

        Trainer result = mapper.toTrainer(dto);

        assertNotNull(result);
        assertNull(result.getUserId());
        assertEquals(DEFAULT_CSV_USERNAME, result.getUsername());
        assertEquals(DEFAULT_CSV_FIRST_NAME, result.getFirstName());
        assertEquals(DEFAULT_CSV_LAST_NAME, result.getLastName());
        assertEquals(DEFAULT_CSV_PASSWORD, result.getPassword());
        assertTrue(result.isActive());
        assertNotNull(result.getSpecialization());
        assertEquals(DEFAULT_CSV_SPECIALIZATION, result.getSpecialization().getTrainingTypeName());
    }

    @Test
    void shouldReturnNullWhenTrainerCsvDtoIsNull() {
        Trainer result = mapper.toTrainer(null);

        assertNull(result);
    }

    @Test
    void shouldMapTrainingCsvDtoToEntity() {
        TrainingCsvDto dto = buildTrainingCsvDto();

        Training result = mapper.toTraining(dto);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals(DEFAULT_CSV_TRAINEE_ID, result.getTraineeId());
        assertEquals(DEFAULT_CSV_TRAINER_ID, result.getTrainerId());
        assertEquals(DEFAULT_CSV_TRAINING_NAME, result.getTrainingName());
        assertNotNull(result.getTrainingType());
        assertEquals(DEFAULT_CSV_TRAINING_TYPE, result.getTrainingType().getTrainingTypeName());
        assertEquals(DEFAULT_CSV_TRAINING_DURATION, result.getTrainingDuration());
        assertEquals(DEFAULT_CSV_TRAINING_DATE, result.getTrainingDate());
    }

    @Test
    void shouldReturnNullWhenTrainingCsvDtoIsNull() {
        Training result = mapper.toTraining(null);

        assertNull(result);
    }

}
