package com.gym.crm.factory;

import com.gym.crm.dto.TraineeCreateDto;
import com.gym.crm.dto.TraineeCreateResponseDto;
import com.gym.crm.dto.TraineeResponseDto;
import com.gym.crm.dto.TraineeUpdateDto;
import com.gym.crm.entity.Trainee;

import java.time.LocalDate;

public class TraineeTestFactory {

    public static final Long DEFAULT_TRAINEE_ID = 1L;
    public static final Long SECONDARY_TRAINEE_ID = 2L;
    public static final Long NON_EXISTENT_TRAINEE_ID = 999L;
    public static final String DEFAULT_USERNAME = "liam.miller";
    public static final String DEFAULT_FIRST_NAME = "Liam";
    public static final String DEFAULT_LAST_NAME = "Miller";
    public static final String DEFAULT_PASSWORD = "password123";
    public static final boolean DEFAULT_ACTIVE = true;
    public static final String DEFAULT_ADDRESS = "123 Main St";
    public static final LocalDate DEFAULT_DATE_OF_BIRTH = LocalDate.of(1990, 1, 1);

    public static Trainee.TraineeBuilder<?, ?> getDefaultTraineeBuilder() {
        return Trainee.builder()
                .username(DEFAULT_USERNAME)
                .firstName(DEFAULT_FIRST_NAME)
                .lastName(DEFAULT_LAST_NAME)
                .password(DEFAULT_PASSWORD)
                .isActive(DEFAULT_ACTIVE)
                .address(DEFAULT_ADDRESS)
                .dateOfBirth(DEFAULT_DATE_OF_BIRTH);
    }

    public static Trainee buildTrainee(Long userId, String username) {
        return getDefaultTraineeBuilder()
                .userId(userId)
                .username(username)
                .build();
    }

    public static Trainee buildTrainee() {
        return getDefaultTraineeBuilder().build();
    }

    public static Trainee buildTrainee(Long userId, String username, String firstName, String lastName) {
        return getDefaultTraineeBuilder()
                .userId(userId)
                .username(username)
                .firstName(firstName)
                .lastName(lastName)
                .build();
    }

    public static Trainee buildTraineeWithId(Long userId) {
        return getDefaultTraineeBuilder()
                .userId(userId)
                .build();
    }

    public static Trainee buildTraineeWithUsername(String username) {
        return getDefaultTraineeBuilder()
                .username(username)
                .build();
    }

    public static Trainee buildTraineeWithoutCredentials() {
        return getDefaultTraineeBuilder()
                .username(null)
                .password(null)
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
                .password("newPassword")
                .active(false)
                .address("456 Oak Ave")
                .dateOfBirth(LocalDate.of(1995, 5, 15));
    }

    public static TraineeUpdateDto buildTraineeUpdateDto() {
        return getDefaultTraineeUpdateDtoBuilder().build();
    }

    public static TraineeResponseDto.TraineeResponseDtoBuilder getDefaultTraineeResponseDtoBuilder() {
        return TraineeResponseDto.builder()
                .userId(DEFAULT_TRAINEE_ID)
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
                .userId(DEFAULT_TRAINEE_ID)
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
