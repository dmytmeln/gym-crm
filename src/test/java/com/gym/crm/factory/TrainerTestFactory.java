package com.gym.crm.factory;

import com.gym.crm.dto.TrainerCreateDto;
import com.gym.crm.dto.TrainerCreateResponseDto;
import com.gym.crm.dto.TrainerResponseDto;
import com.gym.crm.dto.TrainerUpdateDto;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.TrainingType;
import com.gym.crm.entity.User;

public class TrainerTestFactory {

    public static final Long DEFAULT_TRAINER_ID = 1L;
    public static final Long SECONDARY_TRAINER_ID = 2L;
    public static final Long NON_EXISTENT_TRAINER_ID = 999L;
    public static final Long DEFAULT_USER_ID = 1L;
    public static final String DEFAULT_USERNAME = "marcus.stone";
    public static final String DEFAULT_FIRST_NAME = "Marcus";
    public static final String DEFAULT_LAST_NAME = "Stone";
    public static final String DEFAULT_PASSWORD = "password123";
    public static final boolean DEFAULT_ACTIVE = true;
    public static final Long DEFAULT_SPECIALIZATION_ID = 1L;
    public static final String DEFAULT_SPECIALIZATION = "CARDIO";

    public static Trainer.TrainerBuilder getDefaultTrainerBuilder() {
        return Trainer.builder()
                .id(DEFAULT_TRAINER_ID)
                .user(buildUser())
                .specialization(buildTrainingType());
    }

    public static TrainingType.TrainingTypeBuilder getDefaultTrainingTypeBuilder() {
        return TrainingType.builder()
                .id(DEFAULT_SPECIALIZATION_ID)
                .trainingTypeName(DEFAULT_SPECIALIZATION);
    }

    public static User.UserBuilder getDefaultUserBuilder() {
        return User.builder()
                .username(DEFAULT_USERNAME)
                .firstName(DEFAULT_FIRST_NAME)
                .lastName(DEFAULT_LAST_NAME)
                .password(DEFAULT_PASSWORD)
                .isActive(DEFAULT_ACTIVE);
    }

    public static Trainer buildTrainer() {
        return getDefaultTrainerBuilder().build();
    }

    public static User buildUser() {
        return getDefaultUserBuilder().build();
    }

    public static TrainingType buildTrainingType() {
        return getDefaultTrainingTypeBuilder().build();
    }

    public static User buildUser(String username, String firstName, String lastName) {
        return getDefaultUserBuilder()
                .username(username)
                .firstName(firstName)
                .lastName(lastName)
                .build();
    }

    public static User buildUser(String username) {
        return getDefaultUserBuilder()
                .username(username)
                .build();
    }

    public static User buildUserWithoutCredentials() {
        return getDefaultUserBuilder()
                .username(null)
                .password(null)
                .build();
    }

    public static Trainer buildTrainer(Long trainerId, String username) {
        return getDefaultTrainerBuilder()
                .id(trainerId)
                .user(buildUser(username))
                .build();
    }

    public static Trainer buildTrainer(Long trainerId, String username, String firstName, String lastName) {
        return getDefaultTrainerBuilder()
                .id(trainerId)
                .user(buildUser(username, firstName, lastName))
                .build();
    }

    public static Trainer buildTrainer(Long trainerId,
                                       String username,
                                       String firstName,
                                       String lastName,
                                       String specializationType) {
        return getDefaultTrainerBuilder()
                .id(trainerId)
                .user(buildUser(username, firstName, lastName))
                .specialization(TrainingType.builder().trainingTypeName(specializationType).build())
                .build();
    }

    public static Trainer buildTrainerWithId(Long trainerId) {
        return getDefaultTrainerBuilder()
                .id(trainerId)
                .build();
    }

    public static Trainer buildTrainerWithId() {
        return getDefaultTrainerBuilder()
                .id(DEFAULT_TRAINER_ID)
                .build();
    }

    public static Trainer buildTrainerWithIdAndUserId() {
        return getDefaultTrainerBuilder()
                .id(DEFAULT_TRAINER_ID)
                .user(getDefaultUserBuilder().id(DEFAULT_USER_ID).build())
                .build();
    }

    public static Trainer buildTrainerWithUsername(String username) {
        return getDefaultTrainerBuilder()
                .user(buildUser(username))
                .build();
    }

    public static Trainer buildTrainerWithoutCredentials() {
        return getDefaultTrainerBuilder()
                .user(buildUserWithoutCredentials())
                .build();
    }

    public static TrainerCreateDto.TrainerCreateDtoBuilder getDefaultTrainerCreateDtoBuilder() {
        return TrainerCreateDto.builder()
                .firstName(DEFAULT_FIRST_NAME)
                .lastName(DEFAULT_LAST_NAME)
                .active(DEFAULT_ACTIVE)
                .specializationId(DEFAULT_SPECIALIZATION_ID);
    }

    public static TrainerCreateDto buildTrainerCreateDto() {
        return getDefaultTrainerCreateDtoBuilder().build();
    }


    public static TrainerUpdateDto.TrainerUpdateDtoBuilder getDefaultTrainerUpdateDtoBuilder() {
        return TrainerUpdateDto.builder()
                .firstName("Elena")
                .lastName("Rodriguez")
                .active(false)
                .specializationId(DEFAULT_SPECIALIZATION_ID);
    }

    public static TrainerUpdateDto buildTrainerUpdateDto() {
        return getDefaultTrainerUpdateDtoBuilder().build();
    }


    public static TrainerResponseDto.TrainerResponseDtoBuilder getDefaultTrainerResponseDtoBuilder() {
        return TrainerResponseDto.builder()
                .id(DEFAULT_TRAINER_ID)
                .userId(DEFAULT_USER_ID)
                .username(DEFAULT_USERNAME)
                .firstName(DEFAULT_FIRST_NAME)
                .lastName(DEFAULT_LAST_NAME)
                .active(DEFAULT_ACTIVE)
                .specializationName(DEFAULT_SPECIALIZATION);
    }

    public static TrainerResponseDto buildTrainerResponseDto() {
        return getDefaultTrainerResponseDtoBuilder().build();
    }

    public static TrainerCreateResponseDto.TrainerCreateResponseDtoBuilder getDefaultTrainerCreateResponseDtoBuilder() {
        return TrainerCreateResponseDto.builder()
                .id(DEFAULT_TRAINER_ID)
                .userId(DEFAULT_USER_ID)
                .username(DEFAULT_USERNAME)
                .firstName(DEFAULT_FIRST_NAME)
                .lastName(DEFAULT_LAST_NAME)
                .password(DEFAULT_PASSWORD)
                .active(DEFAULT_ACTIVE)
                .specializationName(DEFAULT_SPECIALIZATION);
    }

    public static TrainerCreateResponseDto buildTrainerCreateResponseDto() {
        return getDefaultTrainerCreateResponseDtoBuilder().build();
    }

}
