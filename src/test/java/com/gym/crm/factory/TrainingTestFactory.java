package com.gym.crm.factory;

import com.gym.crm.dto.TrainingCreateDto;
import com.gym.crm.dto.TrainingResponseDto;
import com.gym.crm.entity.Training;
import com.gym.crm.entity.TrainingType;

import java.time.LocalDate;

public class TrainingTestFactory {

    public static final Long DEFAULT_TRAINING_ID = 1L;
    public static final Long SECONDARY_TRAINING_ID = 2L;
    public static final Long NON_EXISTENT_TRAINING_ID = 999L;
    public static final Long DEFAULT_TRAINEE_ID = 1L;
    public static final Long DEFAULT_TRAINER_ID = 2L;
    public static final String DEFAULT_TRAINING_NAME = "Morning Workout";
    public static final String DEFAULT_TRAINING_TYPE = "Cardio";
    public static final int DEFAULT_DURATION = 60;
    public static final LocalDate DEFAULT_DATE = LocalDate.of(2026, 4, 15);

    public static Training.TrainingBuilder defaultTraining() {
        return Training.builder()
                .traineeId(DEFAULT_TRAINEE_ID)
                .trainerId(DEFAULT_TRAINER_ID)
                .trainingName(DEFAULT_TRAINING_NAME)
                .trainingType(new TrainingType(DEFAULT_TRAINING_TYPE))
                .trainingDuration(DEFAULT_DURATION)
                .trainingDate(DEFAULT_DATE);
    }

    public static Training training() {
        return defaultTraining().build();
    }

    public static Training training(Long id, Long traineeId, Long trainerId) {
        return defaultTraining()
                .id(id)
                .traineeId(traineeId)
                .trainerId(trainerId)
                .build();
    }

    public static Training training(Long id, String trainingName) {
        return defaultTraining()
                .id(id)
                .trainingName(trainingName)
                .build();
    }

    public static Training trainingWithId(Long trainingId) {
        return defaultTraining().id(trainingId).build();
    }

    public static Training trainingWithIdAndType(Long trainingId, TrainingType trainingType) {
        return defaultTraining().id(trainingId).trainingType(trainingType).build();
    }

    public static Training trainingWithoutId(Long traineeId, Long trainerId) {
        return defaultTraining()
                .traineeId(traineeId)
                .trainerId(trainerId)
                .build();
    }

    public static TrainingCreateDto.TrainingCreateDtoBuilder defaultTrainingCreateDto() {
        return TrainingCreateDto.builder()
                .traineeId(DEFAULT_TRAINEE_ID)
                .trainerId(DEFAULT_TRAINER_ID)
                .trainingName("Morning Cardio")
                .trainingType(new TrainingType(DEFAULT_TRAINING_TYPE))
                .trainingDuration(DEFAULT_DURATION)
                .trainingDate(LocalDate.of(2026, 4, 17));
    }

    public static TrainingCreateDto trainingCreateDto() {
        return defaultTrainingCreateDto().build();
    }

    public static TrainingCreateDto trainingCreateDto(Long traineeId, Long trainerId) {
        return defaultTrainingCreateDto()
                .traineeId(traineeId)
                .trainerId(trainerId)
                .build();
    }


    public static TrainingResponseDto.TrainingResponseDtoBuilder defaultTrainingResponseDto() {
        return TrainingResponseDto.builder()
                .id(DEFAULT_TRAINING_ID)
                .traineeId(DEFAULT_TRAINEE_ID)
                .trainerId(DEFAULT_TRAINER_ID)
                .trainingName(DEFAULT_TRAINING_NAME)
                .trainingType(new TrainingType(DEFAULT_TRAINING_TYPE))
                .trainingDuration(DEFAULT_DURATION)
                .trainingDate(DEFAULT_DATE);
    }

    public static TrainingResponseDto trainingResponseDto() {
        return defaultTrainingResponseDto().build();
    }

}
