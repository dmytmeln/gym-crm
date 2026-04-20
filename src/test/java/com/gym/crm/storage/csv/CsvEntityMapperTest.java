package com.gym.crm.storage.csv;

import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
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
import static com.gym.crm.factory.CsvDtoTestFactory.traineeCsvDto;
import static com.gym.crm.factory.CsvDtoTestFactory.trainerCsvDto;
import static com.gym.crm.factory.CsvDtoTestFactory.trainingCsvDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CsvEntityMapperTest {

    private CsvEntityMapper mapper;

    @BeforeEach
    public void setUp() {
        mapper = Mappers.getMapper(CsvEntityMapper.class);
    }

    @Test
    public void shouldMapTraineeCsvDtoToEntity() {
        TraineeCsvDto dto = traineeCsvDto();

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
    public void shouldReturnNullWhenTraineeCsvDtoIsNull() {
        Trainee result = mapper.toTrainee(null);

        assertNull(result);
    }

    @Test
    public void shouldMapTrainerCsvDtoToEntity() {
        TrainerCsvDto dto = trainerCsvDto();

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
    public void shouldReturnNullWhenTrainerCsvDtoIsNull() {
        Trainer result = mapper.toTrainer(null);

        assertNull(result);
    }

    @Test
    public void shouldMapTrainingCsvDtoToEntity() {
        TrainingCsvDto dto = trainingCsvDto();

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
    public void shouldReturnNullWhenTrainingCsvDtoIsNull() {
        Training result = mapper.toTraining(null);

        assertNull(result);
    }

}
