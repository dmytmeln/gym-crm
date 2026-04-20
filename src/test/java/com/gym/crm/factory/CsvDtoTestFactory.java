package com.gym.crm.factory;

import com.gym.crm.storage.csv.dto.TraineeCsvDto;
import com.gym.crm.storage.csv.dto.TrainerCsvDto;
import com.gym.crm.storage.csv.dto.TrainingCsvDto;

import java.time.LocalDate;

public class CsvDtoTestFactory {

    public static final String DEFAULT_CSV_USERNAME = "john.doe";
    public static final String DEFAULT_CSV_FIRST_NAME = "John";
    public static final String DEFAULT_CSV_LAST_NAME = "Doe";
    public static final String DEFAULT_CSV_PASSWORD = "pass123";
    public static final boolean DEFAULT_CSV_ACTIVE = true;
    public static final String DEFAULT_CSV_ADDRESS = "123 Main St";
    public static final LocalDate DEFAULT_CSV_DATE_OF_BIRTH = LocalDate.of(1990, 1, 1);
    public static final String DEFAULT_CSV_SPECIALIZATION = "Cardio";
    public static final Long DEFAULT_CSV_TRAINEE_ID = 100L;
    public static final Long DEFAULT_CSV_TRAINER_ID = 200L;
    public static final String DEFAULT_CSV_TRAINING_NAME = "Morning Workout";
    public static final String DEFAULT_CSV_TRAINING_TYPE = "Strength";
    public static final int DEFAULT_CSV_TRAINING_DURATION = 60;
    public static final LocalDate DEFAULT_CSV_TRAINING_DATE = LocalDate.of(2026, 4, 19);

    public static TraineeCsvDto traineeCsvDto() {
        TraineeCsvDto dto = new TraineeCsvDto();
        dto.setUsername(DEFAULT_CSV_USERNAME);
        dto.setFirstName(DEFAULT_CSV_FIRST_NAME);
        dto.setLastName(DEFAULT_CSV_LAST_NAME);
        dto.setPassword(DEFAULT_CSV_PASSWORD);
        dto.setIsActive(DEFAULT_CSV_ACTIVE);
        dto.setAddress(DEFAULT_CSV_ADDRESS);
        dto.setDateOfBirth(DEFAULT_CSV_DATE_OF_BIRTH);
        return dto;
    }

    public static TrainerCsvDto trainerCsvDto() {
        TrainerCsvDto dto = new TrainerCsvDto();
        dto.setUsername(DEFAULT_CSV_USERNAME);
        dto.setFirstName(DEFAULT_CSV_FIRST_NAME);
        dto.setLastName(DEFAULT_CSV_LAST_NAME);
        dto.setPassword(DEFAULT_CSV_PASSWORD);
        dto.setIsActive(DEFAULT_CSV_ACTIVE);
        dto.setSpecializationType(DEFAULT_CSV_SPECIALIZATION);
        return dto;
    }

    public static TrainingCsvDto trainingCsvDto() {
        TrainingCsvDto dto = new TrainingCsvDto();
        dto.setTraineeId(DEFAULT_CSV_TRAINEE_ID);
        dto.setTrainerId(DEFAULT_CSV_TRAINER_ID);
        dto.setTrainingName(DEFAULT_CSV_TRAINING_NAME);
        dto.setTrainingTypeName(DEFAULT_CSV_TRAINING_TYPE);
        dto.setTrainingDuration(DEFAULT_CSV_TRAINING_DURATION);
        dto.setTrainingDate(DEFAULT_CSV_TRAINING_DATE);
        return dto;
    }

}
