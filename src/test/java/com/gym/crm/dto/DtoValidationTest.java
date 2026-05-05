package com.gym.crm.dto;

import com.gym.crm.dto.filter.TraineeTrainingSearchFilter;
import com.gym.crm.dto.filter.TrainerTrainingSearchFilter;
import com.gym.crm.dto.filter.TrainingSearchFilter;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class DtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @ParameterizedTest
    @MethodSource("traineeCreateDtoProvider")
    void shouldValidateTraineeCreateDto(TraineeCreateDto dto, boolean isValid, String expectedMessage) {
        Set<ConstraintViolation<TraineeCreateDto>> violations = validator.validate(dto);

        assertThat(violations.isEmpty()).isEqualTo(isValid);
        if (expectedMessage != null) {
            assertThat(violations)
                    .extracting(ConstraintViolation::getMessage)
                    .contains(expectedMessage);
        }
    }

    @ParameterizedTest
    @MethodSource("trainerCreateDtoProvider")
    void shouldValidateTrainerCreateDto(TrainerCreateDto dto, boolean isValid, String expectedMessage) {
        Set<ConstraintViolation<TrainerCreateDto>> violations = validator.validate(dto);

        assertThat(violations.isEmpty()).isEqualTo(isValid);
        if (expectedMessage != null) {
            assertThat(violations)
                    .extracting(ConstraintViolation::getMessage)
                    .contains(expectedMessage);
        }
    }

    @ParameterizedTest
    @MethodSource("trainingCreateDtoProvider")
    void shouldValidateTrainingCreateDto(TrainingCreateDto dto, boolean isValid, String expectedMessage) {
        Set<ConstraintViolation<TrainingCreateDto>> violations = validator.validate(dto);

        assertThat(violations.isEmpty()).isEqualTo(isValid);
        if (expectedMessage != null) {
            assertThat(violations)
                    .extracting(ConstraintViolation::getMessage)
                    .contains(expectedMessage);
        }
    }

    @ParameterizedTest
    @MethodSource("passwordUpdateDtoProvider")
    void shouldValidatePasswordUpdateDto(PasswordUpdateDto dto, boolean isValid, String expectedMessage) {
        Set<ConstraintViolation<PasswordUpdateDto>> violations = validator.validate(dto);

        assertThat(violations.isEmpty()).isEqualTo(isValid);
        if (expectedMessage != null) {
            assertThat(violations)
                    .extracting(ConstraintViolation::getMessage)
                    .contains(expectedMessage);
        }
    }

    @ParameterizedTest
    @MethodSource("trainingSearchFilterProvider")
    void shouldValidateTrainingSearchFilter(TrainingSearchFilter filter, boolean isValid, String expectedMessage) {
        Set<ConstraintViolation<TrainingSearchFilter>> violations = validator.validate(filter);

        assertThat(violations.isEmpty()).isEqualTo(isValid);
        if (!isValid && expectedMessage != null) {
            assertThat(violations)
                    .extracting(ConstraintViolation::getMessage)
                    .contains(expectedMessage);
        }
    }

    private static Stream<Arguments> traineeCreateDtoProvider() {
        return Stream.of(
                Arguments.of(TraineeCreateDto.builder().firstName("Liam").lastName("Miller").build(), true, null),
                Arguments.of(TraineeCreateDto.builder().firstName("").lastName("Miller").build(), false, "Trainee first name is required."),
                Arguments.of(TraineeCreateDto.builder().firstName("Liam").lastName("").build(), false, "Trainee last name is required."),
                Arguments.of(TraineeCreateDto.builder().firstName("A".repeat(101)).lastName("Miller").build(), false, "Trainee first name must not exceed 100 characters."),
                Arguments.of(TraineeCreateDto.builder().firstName("Liam").lastName("A".repeat(101)).build(), false, "Trainee last name must not exceed 100 characters."),
                Arguments.of(TraineeCreateDto.builder().firstName("Liam").lastName("Miller").address("A".repeat(101)).build(), false, "Address must not exceed 100 characters."),
                Arguments.of(TraineeCreateDto.builder().firstName(null).lastName("Miller").build(), false, "Trainee first name is required."),
                Arguments.of(TraineeCreateDto.builder().firstName("Liam").lastName(null).build(), false, "Trainee last name is required.")
        );
    }

    private static Stream<Arguments> trainerCreateDtoProvider() {
        return Stream.of(
                Arguments.of(TrainerCreateDto.builder().firstName("Liam").lastName("Miller").specializationId(1L).build(), true, null),
                Arguments.of(TrainerCreateDto.builder().firstName("").lastName("Miller").specializationId(1L).build(), false, "Trainer first name is required."),
                Arguments.of(TrainerCreateDto.builder().firstName("Liam").lastName("").specializationId(1L).build(), false, "Trainer last name is required."),
                Arguments.of(TrainerCreateDto.builder().firstName("Liam").lastName("Miller").build(), false, "Specialization is required for trainer."),
                Arguments.of(TrainerCreateDto.builder().firstName("A".repeat(101)).lastName("Miller").specializationId(1L).build(), false, "Trainer first name must not exceed 100 characters."),
                Arguments.of(TrainerCreateDto.builder().firstName("Liam").lastName("A".repeat(101)).specializationId(1L).build(), false, "Trainer last name must not exceed 100 characters."),
                Arguments.of(TrainerCreateDto.builder().firstName(null).lastName("Miller").specializationId(1L).build(), false, "Trainer first name is required."),
                Arguments.of(TrainerCreateDto.builder().firstName("Liam").lastName(null).specializationId(1L).build(), false, "Trainer last name is required.")
        );
    }

    private static Stream<Arguments> trainingCreateDtoProvider() {
        return Stream.of(
                Arguments.of(TrainingCreateDto.builder()
                        .traineeId(1L).trainerId(1L).trainingTypeId(1L)
                        .trainingName("Yoga").trainingDuration(60).trainingDate(LocalDate.now())
                        .build(), true, null),
                Arguments.of(TrainingCreateDto.builder().trainingName("").build(), false, "Training name is required."),
                Arguments.of(TrainingCreateDto.builder().trainingDuration(0).build(), false, "Training duration must be a positive number."),
                Arguments.of(TrainingCreateDto.builder().trainingDate(null).build(), false, "Training date is required.")
        );
    }

    private static Stream<Arguments> passwordUpdateDtoProvider() {
        return Stream.of(
                Arguments.of(PasswordUpdateDto.builder().password("SecurePass123").build(), true, null),
                Arguments.of(PasswordUpdateDto.builder().password("short").build(), false, "Password must be at least 10 characters long."),
                Arguments.of(PasswordUpdateDto.builder().password("").build(), false, "Password is required."),
                Arguments.of(PasswordUpdateDto.builder().password(null).build(), false, "Password is required.")
        );
    }

    private static Stream<Arguments> trainingSearchFilterProvider() {
        return Stream.of(
                Arguments.of(TrainingSearchFilter.builder().username("user").build(), true, null),
                Arguments.of(TrainingSearchFilter.builder().username("").build(), false, "Username is required for search."),
                Arguments.of(TrainingSearchFilter.builder().username(null).build(), false, "Username is required for search."),
                Arguments.of(TrainerTrainingSearchFilter.builder().username("user").build(), true, null),
                Arguments.of(TrainerTrainingSearchFilter.builder().username("").build(), false, "Username is required for search."),
                Arguments.of(TrainerTrainingSearchFilter.builder().username(null).build(), false, "Username is required for search."),
                Arguments.of(TraineeTrainingSearchFilter.builder().username("user").build(), true, null),
                Arguments.of(TraineeTrainingSearchFilter.builder().username("").build(), false, "Username is required for search."),
                Arguments.of(TraineeTrainingSearchFilter.builder().username(null).build(), false, "Username is required for search.")
        );
    }

}
