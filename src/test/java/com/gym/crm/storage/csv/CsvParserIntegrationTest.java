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
class CsvParserIntegrationTest {

    @Autowired
    private CsvParser parser;

    @Test
    void shouldParseTraineeCsvFileFromTestResources() {
        List<TraineeCsvDto> result = parser.parseCsv("classpath:test-data/trainee.csv", TraineeCsvDto.class);

        assertNotNull(result);
        assertEquals(3, result.size());

        TraineeCsvDto firstTrainee = result.get(0);
        assertEquals("liam.miller", firstTrainee.getUsername());
        assertEquals("Liam", firstTrainee.getFirstName());
        assertEquals("Miller", firstTrainee.getLastName());
        assertEquals("password123", firstTrainee.getPassword());
        assertTrue(firstTrainee.getIsActive());
        assertEquals("123 Main St", firstTrainee.getAddress());
        assertEquals(LocalDate.of(1990, 5, 15), firstTrainee.getDateOfBirth());

        TraineeCsvDto secondTrainee = result.get(1);
        assertEquals("sophia.wilson", secondTrainee.getUsername());
        assertEquals("Sophia", secondTrainee.getFirstName());
        assertEquals("Wilson", secondTrainee.getLastName());
        assertTrue(secondTrainee.getIsActive());

        TraineeCsvDto thirdTrainee = result.get(2);
        assertEquals("bob.wilson", thirdTrainee.getUsername());
        assertFalse(thirdTrainee.getIsActive());
    }

    @Test
    void shouldParseTrainerCsvFileFromTestResources() {
        List<TrainerCsvDto> result = parser.parseCsv("classpath:test-data/trainer.csv", TrainerCsvDto.class);

        assertNotNull(result);
        assertEquals(3, result.size());

        TrainerCsvDto firstTrainer = result.get(0);
        assertEquals("marcus.stone", firstTrainer.getUsername());
        assertEquals("Marcus", firstTrainer.getFirstName());
        assertEquals("Stone", firstTrainer.getLastName());
        assertEquals("trainerpass1", firstTrainer.getPassword());
        assertTrue(firstTrainer.getIsActive());
        assertEquals("CARDIO", firstTrainer.getSpecializationType());

        TrainerCsvDto secondTrainer = result.get(1);
        assertEquals("sarah.adams", secondTrainer.getUsername());
        assertEquals("STRENGTH", secondTrainer.getSpecializationType());

        TrainerCsvDto thirdTrainer = result.get(2);
        assertEquals("alex.morgan", thirdTrainer.getUsername());
        assertEquals("YOGA", thirdTrainer.getSpecializationType());
    }

    @Test
    void shouldParseTrainingCsvFileFromTestResources() {
        List<TrainingCsvDto> result = parser.parseCsv("classpath:test-data/training.csv", TrainingCsvDto.class);

        assertNotNull(result);
        assertEquals(4, result.size());

        TrainingCsvDto firstTraining = result.get(0);
        assertEquals(1L, firstTraining.getTraineeId());
        assertEquals(1L, firstTraining.getTrainerId());
        assertEquals("Morning Cardio Session", firstTraining.getTrainingName());
        assertEquals("CARDIO", firstTraining.getTrainingTypeName());
        assertEquals(60, firstTraining.getTrainingDuration());
        assertEquals(LocalDate.of(2026, 4, 1), firstTraining.getTrainingDate());

        TrainingCsvDto secondTraining = result.get(1);
        assertEquals(2L, secondTraining.getTraineeId());
        assertEquals(2L, secondTraining.getTrainerId());
        assertEquals("Strength Training", secondTraining.getTrainingName());
        assertEquals("STRENGTH", secondTraining.getTrainingTypeName());
        assertEquals(90, secondTraining.getTrainingDuration());
        assertEquals(LocalDate.of(2026, 4, 2), secondTraining.getTrainingDate());

        TrainingCsvDto thirdTraining = result.get(2);
        assertEquals(1L, thirdTraining.getTraineeId());
        assertEquals(3L, thirdTraining.getTrainerId());
        assertEquals("Yoga Class", thirdTraining.getTrainingName());
        assertEquals("YOGA", thirdTraining.getTrainingTypeName());
        assertEquals(45, thirdTraining.getTrainingDuration());
        assertEquals(LocalDate.of(2026, 4, 3), thirdTraining.getTrainingDate());

        TrainingCsvDto fourthTraining = result.get(3);
        assertEquals(3L, fourthTraining.getTraineeId());
        assertEquals(1L, fourthTraining.getTrainerId());
        assertEquals("Evening Run", fourthTraining.getTrainingName());
        assertEquals("CARDIO", fourthTraining.getTrainingTypeName());
        assertEquals(30, fourthTraining.getTrainingDuration());
        assertEquals(LocalDate.of(2026, 4, 5), fourthTraining.getTrainingDate());
    }

}
