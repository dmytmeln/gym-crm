package com.gym.crm.factory;

import com.gym.crm.dto.TrainerCreateDto;
import com.gym.crm.dto.TrainerCreateResponseDto;
import com.gym.crm.dto.TrainerResponseDto;
import com.gym.crm.dto.TrainerUpdateDto;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;

public class TrainerTestFactory {

    public static final Long DEFAULT_TRAINER_ID = 1L;
    public static final Long SECONDARY_TRAINER_ID = 2L;
    public static final Long NON_EXISTENT_TRAINER_ID = 999L;
    public static final String DEFAULT_USERNAME = "marcus.stone";
    public static final String DEFAULT_FIRST_NAME = "Marcus";
    public static final String DEFAULT_LAST_NAME = "Stone";
    public static final String DEFAULT_PASSWORD = "password123";
    public static final boolean DEFAULT_ACTIVE = true;
    public static final String DEFAULT_SPECIALIZATION = "Cardio";

    public static Trainer.TrainerBuilder<?, ?> getDefaultTrainerBuilder() {
        return Trainer.builder()
                .username(DEFAULT_USERNAME)
                .firstName(DEFAULT_FIRST_NAME)
                .lastName(DEFAULT_LAST_NAME)
                .password(DEFAULT_PASSWORD)
                .isActive(DEFAULT_ACTIVE)
                .specialization(new TrainingType(DEFAULT_SPECIALIZATION));
    }

    public static Trainer buildTrainer() {
        return getDefaultTrainerBuilder().build();
    }

    public static Trainer buildTrainer(Long userId, String username) {
        return getDefaultTrainerBuilder()
                .userId(userId)
                .username(username)
                .build();
    }

    public static Trainer buildTrainer(Long userId, String username, String firstName, String lastName) {
        return getDefaultTrainerBuilder()
                .userId(userId)
                .username(username)
                .firstName(firstName)
                .lastName(lastName)
                .build();
    }

    public static Trainer buildTrainer(Long userId,
                                       String username,
                                       String firstName,
                                       String lastName,
                                       String specializationType) {
        return getDefaultTrainerBuilder()
                .userId(userId)
                .username(username)
                .firstName(firstName)
                .lastName(lastName)
                .specialization(new TrainingType(specializationType))
                .build();
    }

    public static Trainer buildTrainerWithId(Long userId) {
        return getDefaultTrainerBuilder()
                .userId(userId)
                .build();
    }

    public static Trainer buildTrainerWithUsername(String username) {
        return getDefaultTrainerBuilder()
                .username(username)
                .build();
    }

    public static Trainer buildTrainerWithoutCredentials() {
        return getDefaultTrainerBuilder()
                .username(null)
                .password(null)
                .build();
    }

    public static TrainerCreateDto.TrainerCreateDtoBuilder getDefaultTrainerCreateDtoBuilder() {
        return TrainerCreateDto.builder()
                .firstName(DEFAULT_FIRST_NAME)
                .lastName(DEFAULT_LAST_NAME)
                .active(DEFAULT_ACTIVE)
                .specialization(new TrainingType(DEFAULT_SPECIALIZATION));
    }

    public static TrainerCreateDto buildTrainerCreateDto() {
        return getDefaultTrainerCreateDtoBuilder().build();
    }


    public static TrainerUpdateDto.TrainerUpdateDtoBuilder getDefaultTrainerUpdateDtoBuilder() {
        return TrainerUpdateDto.builder()
                .firstName("Elena")
                .lastName("Rodriguez")
                .password("newPassword")
                .active(false)
                .specialization(new TrainingType("Strength"));
    }

    public static TrainerUpdateDto buildTrainerUpdateDto() {
        return getDefaultTrainerUpdateDtoBuilder().build();
    }


    public static TrainerResponseDto.TrainerResponseDtoBuilder getDefaultTrainerResponseDtoBuilder() {
        return TrainerResponseDto.builder()
                .userId(DEFAULT_TRAINER_ID)
                .username(DEFAULT_USERNAME)
                .firstName(DEFAULT_FIRST_NAME)
                .lastName(DEFAULT_LAST_NAME)
                .active(DEFAULT_ACTIVE)
                .specialization(new TrainingType(DEFAULT_SPECIALIZATION));
    }

    public static TrainerResponseDto buildTrainerResponseDto() {
        return getDefaultTrainerResponseDtoBuilder().build();
    }

    public static TrainerCreateResponseDto.TrainerCreateResponseDtoBuilder getDefaultTrainerCreateResponseDtoBuilder() {
        return TrainerCreateResponseDto.builder()
                .userId(DEFAULT_TRAINER_ID)
                .username(DEFAULT_USERNAME)
                .firstName(DEFAULT_FIRST_NAME)
                .lastName(DEFAULT_LAST_NAME)
                .password(DEFAULT_PASSWORD)
                .active(DEFAULT_ACTIVE)
                .specialization(new TrainingType(DEFAULT_SPECIALIZATION));
    }

    public static TrainerCreateResponseDto buildTrainerCreateResponseDto() {
        return getDefaultTrainerCreateResponseDtoBuilder().build();
    }

}
