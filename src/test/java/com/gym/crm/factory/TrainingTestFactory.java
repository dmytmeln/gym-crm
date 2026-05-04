package com.gym.crm.factory;

import com.gym.crm.dto.TrainingCreateDto;
import com.gym.crm.dto.TrainingResponseDto;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import com.gym.crm.entity.TrainingType;

import java.time.LocalDate;

public class TrainingTestFactory {

    public static final Long DEFAULT_TRAINING_ID = 1L;
    public static final Long SECONDARY_TRAINING_ID = 2L;
    public static final Long DEFAULT_TRAINEE_ID = 1L;
    public static final Long DEFAULT_TRAINER_ID = 2L;
    public static final String DEFAULT_TRAINING_NAME = "Morning Workout";
    public static final int DEFAULT_DURATION = 60;
    public static final LocalDate DEFAULT_DATE = LocalDate.of(2026, 4, 15);
    public static final Long DEFAULT_TRAINING_TYPE_ID = 1L;
    public static final String DEFAULT_TRAINING_TYPE_NAME = "STRENGTH";

    public static Training.TrainingBuilder getDefaultTrainingBuilder() {
        return Training.builder()
                .trainee(Trainee.builder().id(DEFAULT_TRAINEE_ID).build())
                .trainer(Trainer.builder().id(DEFAULT_TRAINER_ID).build())
                .trainingName(DEFAULT_TRAINING_NAME)
                .trainingType(TrainingType.builder().
                        id(DEFAULT_TRAINING_TYPE_ID)
                        .trainingTypeName(DEFAULT_TRAINING_TYPE_NAME).
                        build())
                .trainingDuration(DEFAULT_DURATION)
                .trainingDate(DEFAULT_DATE);
    }

    public static Training buildTraining() {
        return getDefaultTrainingBuilder().build();
    }

    public static Training buildTraining(Long id, Long traineeId, Long trainerId) {
        return getDefaultTrainingBuilder()
                .id(id)
                .trainee(Trainee.builder().id(traineeId).build())
                .trainer(Trainer.builder().id(trainerId).build())
                .build();
    }

    public static Training buildTraining(Long id, String trainingName) {
        return getDefaultTrainingBuilder()
                .id(id)
                .trainingName(trainingName)
                .build();
    }

    public static Training buildTrainingWithId(Long trainingId) {
        return getDefaultTrainingBuilder().id(trainingId).build();
    }

    public static Training buildTrainingWithIdAndTypeName(Long trainingId, String trainingTypeName) {
        return getDefaultTrainingBuilder()
                .id(trainingId)
                .trainingType(TrainingType.builder().trainingTypeName(trainingTypeName).build())
                .build();
    }

    public static Training buildTrainingWithoutId(Long traineeId, Long trainerId) {
        return getDefaultTrainingBuilder()
                .trainee(Trainee.builder().id(traineeId).build())
                .trainer(Trainer.builder().id(trainerId).build())
                .build();
    }

    public static TrainingCreateDto.TrainingCreateDtoBuilder getDefaultTrainingCreateDtoBuilder() {
        return TrainingCreateDto.builder()
                .traineeId(DEFAULT_TRAINEE_ID)
                .trainerId(DEFAULT_TRAINER_ID)
                .trainingName("Morning Cardio")
                .trainingTypeId(DEFAULT_TRAINING_TYPE_ID)
                .trainingDuration(DEFAULT_DURATION)
                .trainingDate(LocalDate.of(2026, 4, 17));
    }

    public static TrainingCreateDto buildTrainingCreateDto() {
        return getDefaultTrainingCreateDtoBuilder().build();
    }

    public static TrainingCreateDto buildTrainingCreateDto(Long traineeId, Long trainerId) {
        return getDefaultTrainingCreateDtoBuilder()
                .traineeId(traineeId)
                .trainerId(trainerId)
                .build();
    }

    public static TrainingResponseDto.TrainingResponseDtoBuilder getDefaultTrainingResponseDtoBuilder() {
        return TrainingResponseDto.builder()
                .id(DEFAULT_TRAINING_ID)
                .traineeId(DEFAULT_TRAINEE_ID)
                .trainerId(DEFAULT_TRAINER_ID)
                .trainingName(DEFAULT_TRAINING_NAME)
                .trainingTypeName(DEFAULT_TRAINING_TYPE_NAME)
                .trainingDuration(DEFAULT_DURATION)
                .trainingDate(DEFAULT_DATE);
    }

    public static TrainingResponseDto buildTrainingResponseDto() {
        return getDefaultTrainingResponseDtoBuilder().build();
    }

}
