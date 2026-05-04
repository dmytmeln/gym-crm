package com.gym.crm.factory;

import com.gym.crm.dto.TraineeCreateDto;
import com.gym.crm.dto.TraineeCreateResponseDto;
import com.gym.crm.dto.TraineeResponseDto;
import com.gym.crm.dto.TraineeUpdateDto;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.User;

import java.time.LocalDate;

public class TraineeTestFactory {

    public static final Long DEFAULT_TRAINEE_ID = 1L;
    public static final Long SECONDARY_TRAINEE_ID = 2L;
    public static final Long NON_EXISTENT_TRAINEE_ID = 999L;
    public static final Long DEFAULT_USER_ID = 1L;
    public static final String DEFAULT_USERNAME = "liam.miller";
    public static final String DEFAULT_FIRST_NAME = "Liam";
    public static final String DEFAULT_LAST_NAME = "Miller";
    public static final String DEFAULT_PASSWORD = "password123";
    public static final boolean DEFAULT_ACTIVE = true;
    public static final String DEFAULT_ADDRESS = "123 Main St";
    public static final LocalDate DEFAULT_DATE_OF_BIRTH = LocalDate.of(1990, 1, 1);

    public static Trainee.TraineeBuilder getDefaultTraineeBuilder() {
        return Trainee.builder()
                .user(buildUser())
                .address(DEFAULT_ADDRESS)
                .dateOfBirth(DEFAULT_DATE_OF_BIRTH);
    }

    public static User.UserBuilder getDefaultUserBuilder() {
        return User.builder()
                .username(DEFAULT_USERNAME)
                .firstName(DEFAULT_FIRST_NAME)
                .lastName(DEFAULT_LAST_NAME)
                .password(DEFAULT_PASSWORD)
                .isActive(DEFAULT_ACTIVE);
    }

    public static Trainee buildTrainee(Long id, String username) {
        return getDefaultTraineeBuilder()
                .id(id)
                .user(buildUser(username))
                .build();
    }

    public static User buildUser() {
        return getDefaultUserBuilder().build();
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

    public static Trainee buildTrainee() {
        return getDefaultTraineeBuilder().build();
    }

    public static Trainee buildTrainee(Long id, String username, String firstName, String lastName) {
        return getDefaultTraineeBuilder()
                .id(id)
                .user(buildUser(username, firstName, lastName))
                .build();
    }

    public static Trainee buildTraineeWithId(Long id) {
        return getDefaultTraineeBuilder()
                .id(id)
                .build();
    }

    public static Trainee buildTraineeWithId() {
        return getDefaultTraineeBuilder()
                .id(DEFAULT_TRAINEE_ID)
                .user(getDefaultUserBuilder().id(DEFAULT_USER_ID).build())
                .build();
    }

    public static Trainee buildTraineeWithIdAndUserId() {
        return getDefaultTraineeBuilder()
                .id(DEFAULT_TRAINEE_ID)
                .user(getDefaultUserBuilder().id(DEFAULT_USER_ID).build())
                .build();
    }

    public static Trainee buildTraineeWithUsername(String username) {
        return getDefaultTraineeBuilder()
                .user(buildUser(username))
                .build();
    }

    public static Trainee buildTraineeWithoutCredentials() {
        return getDefaultTraineeBuilder()
                .user(buildUserWithoutCredentials())
                .build();
    }

    public static Trainee buildTraineeWithoutUser() {
        return getDefaultTraineeBuilder()
                .user(null)
                .build();
    }

    public static TraineeCreateDto.TraineeCreateDtoBuilder getDefaultTraineeCreateDtoBuilder() {
        return TraineeCreateDto.builder()
                .firstName(DEFAULT_FIRST_NAME)
                .lastName(DEFAULT_LAST_NAME)
                .active(DEFAULT_ACTIVE)
                .address(DEFAULT_ADDRESS)
                .dateOfBirth(DEFAULT_DATE_OF_BIRTH);
    }

    public static TraineeCreateDto buildTraineeCreateDto() {
        return getDefaultTraineeCreateDtoBuilder().build();
    }

    public static TraineeUpdateDto.TraineeUpdateDtoBuilder getDefaultTraineeUpdateDtoBuilder() {
        return TraineeUpdateDto.builder()
                .firstName("Sophia")
                .lastName("Wilson")
                .active(false)
                .address("456 Oak Ave")
                .dateOfBirth(LocalDate.of(1995, 5, 15));
    }

    public static TraineeUpdateDto buildTraineeUpdateDto() {
        return getDefaultTraineeUpdateDtoBuilder().build();
    }

    public static TraineeResponseDto.TraineeResponseDtoBuilder getDefaultTraineeResponseDtoBuilder() {
        return TraineeResponseDto.builder()
                .id(DEFAULT_TRAINEE_ID)
                .userId(DEFAULT_USER_ID)
                .username(DEFAULT_USERNAME)
                .firstName(DEFAULT_FIRST_NAME)
                .lastName(DEFAULT_LAST_NAME)
                .active(DEFAULT_ACTIVE)
                .address(DEFAULT_ADDRESS)
                .dateOfBirth(DEFAULT_DATE_OF_BIRTH);
    }

    public static TraineeResponseDto buildTraineeResponseDto() {
        return getDefaultTraineeResponseDtoBuilder().build();
    }

    public static TraineeCreateResponseDto.TraineeCreateResponseDtoBuilder getDefaultTraineeCreateResponseDtoBuilder() {
        return TraineeCreateResponseDto.builder()
                .id(DEFAULT_TRAINEE_ID)
                .userId(DEFAULT_USER_ID)
                .username(DEFAULT_USERNAME)
                .firstName(DEFAULT_FIRST_NAME)
                .lastName(DEFAULT_LAST_NAME)
                .password(DEFAULT_PASSWORD)
                .active(DEFAULT_ACTIVE)
                .address(DEFAULT_ADDRESS)
                .dateOfBirth(DEFAULT_DATE_OF_BIRTH);
    }

    public static TraineeCreateResponseDto buildTraineeCreateResponseDto() {
        return getDefaultTraineeCreateResponseDtoBuilder().build();
    }

}
