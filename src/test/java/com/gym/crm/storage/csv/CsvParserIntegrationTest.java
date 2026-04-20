package com.gym.crm.storage.csv;

import com.gym.crm.storage.csv.dto.TraineeCsvDto;
import com.gym.crm.storage.csv.dto.TrainerCsvDto;
import com.gym.crm.storage.csv.dto.TrainingCsvDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(CsvParser.class)
public class CsvParserIntegrationTest {

    @Autowired
    private CsvParser csvParser;

    @Test
    public void shouldParseTraineeCsvFileFromTestResources() {
        List<TraineeCsvDto> result = csvParser.parseCsv("classpath:test-data/trainee.csv", TraineeCsvDto.class);

        assertNotNull(result);
        assertEquals(3, result.size());

        TraineeCsvDto firstTrainee = result.get(0);
        assertEquals("john.doe", firstTrainee.getUsername());
        assertEquals("John", firstTrainee.getFirstName());
        assertEquals("Doe", firstTrainee.getLastName());
        assertEquals("password123", firstTrainee.getPassword());
        assertTrue(firstTrainee.getIsActive());
        assertEquals("123 Main St", firstTrainee.getAddress());
        assertEquals(LocalDate.of(1990, 5, 15), firstTrainee.getDateOfBirth());

        TraineeCsvDto secondTrainee = result.get(1);
        assertEquals("jane.smith", secondTrainee.getUsername());
        assertEquals("Jane", secondTrainee.getFirstName());
        assertEquals("Smith", secondTrainee.getLastName());
        assertTrue(secondTrainee.getIsActive());

        TraineeCsvDto thirdTrainee = result.get(2);
        assertEquals("bob.wilson", thirdTrainee.getUsername());
        assertFalse(thirdTrainee.getIsActive());
    }

    @Test
    public void shouldParseTrainerCsvFileFromTestResources() {
        List<TrainerCsvDto> result = csvParser.parseCsv("classpath:test-data/trainer.csv", TrainerCsvDto.class);

        assertNotNull(result);
        assertEquals(3, result.size());

        TrainerCsvDto firstTrainer = result.get(0);
        assertEquals("mike.trainer", firstTrainer.getUsername());
        assertEquals("Mike", firstTrainer.getFirstName());
        assertEquals("Trainer", firstTrainer.getLastName());
        assertEquals("trainerpass1", firstTrainer.getPassword());
        assertTrue(firstTrainer.getIsActive());
        assertEquals("CARDIO", firstTrainer.getSpecializationType());

        TrainerCsvDto secondTrainer = result.get(1);
        assertEquals("sarah.coach", secondTrainer.getUsername());
        assertEquals("STRENGTH", secondTrainer.getSpecializationType());

        TrainerCsvDto thirdTrainer = result.get(2);
        assertEquals("alex.fitness", thirdTrainer.getUsername());
        assertEquals("YOGA", thirdTrainer.getSpecializationType());
    }

    @Test
    public void shouldParseTrainingCsvFileFromTestResources() {
        List<TrainingCsvDto> result = csvParser.parseCsv("classpath:test-data/training.csv", TrainingCsvDto.class);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

}
