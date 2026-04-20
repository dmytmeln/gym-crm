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
    public static final String DEFAULT_USERNAME = "john.doe";
    public static final String DEFAULT_FIRST_NAME = "John";
    public static final String DEFAULT_LAST_NAME = "Doe";
    public static final String DEFAULT_PASSWORD = "password123";
    public static final boolean DEFAULT_ACTIVE = true;
    public static final String DEFAULT_ADDRESS = "123 Main St";
    public static final LocalDate DEFAULT_DATE_OF_BIRTH = LocalDate.of(1990, 1, 1);

    public static Trainee.TraineeBuilder<?, ?> defaultTrainee() {
        return Trainee.builder()
                .username(DEFAULT_USERNAME)
                .firstName(DEFAULT_FIRST_NAME)
                .lastName(DEFAULT_LAST_NAME)
                .password(DEFAULT_PASSWORD)
                .isActive(DEFAULT_ACTIVE)
                .address(DEFAULT_ADDRESS)
                .dateOfBirth(DEFAULT_DATE_OF_BIRTH);
    }

    public static Trainee trainee(Long userId, String username) {
        return defaultTrainee()
                .userId(userId)
                .username(username)
                .build();
    }

    public static Trainee trainee() {
        return defaultTrainee().build();
    }

    public static Trainee trainee(Long userId, String username, String firstName, String lastName) {
        return defaultTrainee()
                .userId(userId)
                .username(username)
                .firstName(firstName)
                .lastName(lastName)
                .build();
    }

    public static Trainee traineeWithId(Long userId) {
        return defaultTrainee()
                .userId(userId)
                .build();
    }

    public static Trainee traineeWithUsername(String username) {
        return defaultTrainee()
                .username(username)
                .build();
    }

    public static Trainee traineeWithoutCredentials() {
        return defaultTrainee()
                .username(null)
                .password(null)
                .build();
    }

    public static TraineeCreateDto.TraineeCreateDtoBuilder defaultTraineeCreateDto() {
        return TraineeCreateDto.builder()
                .firstName(DEFAULT_FIRST_NAME)
                .lastName(DEFAULT_LAST_NAME)
                .active(DEFAULT_ACTIVE)
                .address(DEFAULT_ADDRESS)
                .dateOfBirth(DEFAULT_DATE_OF_BIRTH);
    }

    public static TraineeCreateDto traineeCreateDto() {
        return defaultTraineeCreateDto().build();
    }

    public static TraineeUpdateDto.TraineeUpdateDtoBuilder defaultTraineeUpdateDto() {
        return TraineeUpdateDto.builder()
                .firstName("Jane")
                .lastName("Smith")
                .password("newPassword")
                .active(false)
                .address("456 Oak Ave")
                .dateOfBirth(LocalDate.of(1995, 5, 15));
    }

    public static TraineeUpdateDto traineeUpdateDto() {
        return defaultTraineeUpdateDto().build();
    }

    public static TraineeResponseDto.TraineeResponseDtoBuilder defaultTraineeResponseDto() {
        return TraineeResponseDto.builder()
                .userId(DEFAULT_TRAINEE_ID)
                .username(DEFAULT_USERNAME)
                .firstName(DEFAULT_FIRST_NAME)
                .lastName(DEFAULT_LAST_NAME)
                .active(DEFAULT_ACTIVE)
                .address(DEFAULT_ADDRESS)
                .dateOfBirth(DEFAULT_DATE_OF_BIRTH);
    }

    public static TraineeResponseDto traineeResponseDto() {
        return defaultTraineeResponseDto().build();
    }

    public static TraineeCreateResponseDto.TraineeCreateResponseDtoBuilder defaultTraineeCreateResponseDto() {
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

    public static TraineeCreateResponseDto traineeCreateResponseDto() {
        return defaultTraineeCreateResponseDto().build();
    }

}
