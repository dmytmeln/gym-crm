package com.gym.crm.factory;

import com.gym.crm.dto.TrainerCreateDto;
import com.gym.crm.dto.TrainerCreateResponseDto;
import com.gym.crm.dto.TrainerResponseDto;
import com.gym.crm.dto.TrainerUpdateDto;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.TrainingType;

public class TrainerTestFactory {

    public static final Long DEFAULT_TRAINER_ID = 1L;
    public static final Long SECONDARY_TRAINER_ID = 2L;
    public static final Long NON_EXISTENT_TRAINER_ID = 999L;
    public static final String DEFAULT_USERNAME = "mike.trainer";
    public static final String DEFAULT_FIRST_NAME = "Mike";
    public static final String DEFAULT_LAST_NAME = "Trainer";
    public static final String DEFAULT_PASSWORD = "password123";
    public static final boolean DEFAULT_ACTIVE = true;
    public static final String DEFAULT_SPECIALIZATION = "Cardio";

    public static Trainer.TrainerBuilder<?, ?> defaultTrainer() {
        return Trainer.builder()
                .username(DEFAULT_USERNAME)
                .firstName(DEFAULT_FIRST_NAME)
                .lastName(DEFAULT_LAST_NAME)
                .password(DEFAULT_PASSWORD)
                .isActive(DEFAULT_ACTIVE)
                .specialization(new TrainingType(DEFAULT_SPECIALIZATION));
    }

    public static Trainer trainer() {
        return defaultTrainer().build();
    }

    public static Trainer trainer(Long userId, String username) {
        return defaultTrainer()
                .userId(userId)
                .username(username)
                .build();
    }

    public static Trainer trainer(Long userId, String username, String firstName, String lastName) {
        return defaultTrainer()
                .userId(userId)
                .username(username)
                .firstName(firstName)
                .lastName(lastName)
                .build();
    }

    public static Trainer trainer(Long userId, String username, String firstName, String lastName, String specializationType) {
        return defaultTrainer()
                .userId(userId)
                .username(username)
                .firstName(firstName)
                .lastName(lastName)
                .specialization(new TrainingType(specializationType))
                .build();
    }

    public static Trainer trainerWithId(Long userId) {
        return defaultTrainer()
                .userId(userId)
                .build();
    }

    public static Trainer trainerWithUsername(String username) {
        return defaultTrainer()
                .username(username)
                .build();
    }

    public static Trainer trainerWithoutCredentials() {
        return defaultTrainer()
                .username(null)
                .password(null)
                .build();
    }

    public static TrainerCreateDto.TrainerCreateDtoBuilder defaultTrainerCreateDto() {
        return TrainerCreateDto.builder()
                .firstName(DEFAULT_FIRST_NAME)
                .lastName(DEFAULT_LAST_NAME)
                .active(DEFAULT_ACTIVE)
                .specialization(new TrainingType(DEFAULT_SPECIALIZATION));
    }

    public static TrainerCreateDto trainerCreateDto() {
        return defaultTrainerCreateDto().build();
    }


    public static TrainerUpdateDto.TrainerUpdateDtoBuilder defaultTrainerUpdateDto() {
        return TrainerUpdateDto.builder()
                .firstName("Michael")
                .lastName("Coach")
                .password("newPassword")
                .active(false)
                .specialization(new TrainingType("Strength"));
    }

    public static TrainerUpdateDto trainerUpdateDto() {
        return defaultTrainerUpdateDto().build();
    }


    public static TrainerResponseDto.TrainerResponseDtoBuilder defaultTrainerResponseDto() {
        return TrainerResponseDto.builder()
                .userId(DEFAULT_TRAINER_ID)
                .username(DEFAULT_USERNAME)
                .firstName(DEFAULT_FIRST_NAME)
                .lastName(DEFAULT_LAST_NAME)
                .active(DEFAULT_ACTIVE)
                .specialization(new TrainingType(DEFAULT_SPECIALIZATION));
    }

    public static TrainerResponseDto trainerResponseDto() {
        return defaultTrainerResponseDto().build();
    }

    public static TrainerCreateResponseDto.TrainerCreateResponseDtoBuilder defaultTrainerCreateResponseDto() {
        return TrainerCreateResponseDto.builder()
                .userId(DEFAULT_TRAINER_ID)
                .username(DEFAULT_USERNAME)
                .firstName(DEFAULT_FIRST_NAME)
                .lastName(DEFAULT_LAST_NAME)
                .password(DEFAULT_PASSWORD)
                .active(DEFAULT_ACTIVE)
                .specialization(new TrainingType(DEFAULT_SPECIALIZATION));
    }

    public static TrainerCreateResponseDto trainerCreateResponseDto() {
        return defaultTrainerCreateResponseDto().build();
    }

}
