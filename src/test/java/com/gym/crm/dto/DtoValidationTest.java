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

class DtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @ParameterizedTest
    @MethodSource("traineeCreateDtoValidProvider")
    void shouldValidateTraineeCreateDtoIsValid(TraineeCreateDto dto) {
        Set<ConstraintViolation<TraineeCreateDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("traineeCreateDtoInvalidProvider")
    void shouldValidateTraineeCreateDtoHasExpectedError(TraineeCreateDto dto, String expectedMessage) {
        Set<ConstraintViolation<TraineeCreateDto>> violations = validator.validate(dto);

        assertThat(violations)
                .isNotEmpty()
                .extracting(ConstraintViolation::getMessage)
                .contains(expectedMessage);
    }

    @ParameterizedTest
    @MethodSource("trainerCreateDtoValidProvider")
    void shouldValidateTrainerCreateDtoIsValid(TrainerCreateDto dto) {
        Set<ConstraintViolation<TrainerCreateDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("trainerCreateDtoInvalidProvider")
    void shouldValidateTrainerCreateDtoHasExpectedError(TrainerCreateDto dto, String expectedMessage) {
        Set<ConstraintViolation<TrainerCreateDto>> violations = validator.validate(dto);

        assertThat(violations)
                .isNotEmpty()
                .extracting(ConstraintViolation::getMessage)
                .contains(expectedMessage);
    }

    @ParameterizedTest
    @MethodSource("trainingCreateDtoValidProvider")
    void shouldValidateTrainingCreateDtoIsValid(TrainingCreateDto dto) {
        Set<ConstraintViolation<TrainingCreateDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("trainingCreateDtoInvalidProvider")
    void shouldValidateTrainingCreateDtoHasExpectedError(TrainingCreateDto dto, String expectedMessage) {
        Set<ConstraintViolation<TrainingCreateDto>> violations = validator.validate(dto);

        assertThat(violations)
                .isNotEmpty()
                .extracting(ConstraintViolation::getMessage)
                .contains(expectedMessage);
    }

    @ParameterizedTest
    @MethodSource("passwordUpdateDtoValidProvider")
    void shouldValidatePasswordUpdateDtoIsValid(PasswordUpdateDto dto) {
        Set<ConstraintViolation<PasswordUpdateDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("passwordUpdateDtoInvalidProvider")
    void shouldValidatePasswordUpdateDtoHasExpectedError(PasswordUpdateDto dto, String expectedMessage) {
        Set<ConstraintViolation<PasswordUpdateDto>> violations = validator.validate(dto);

        assertThat(violations)
                .isNotEmpty()
                .extracting(ConstraintViolation::getMessage)
                .contains(expectedMessage);
    }

    @ParameterizedTest
    @MethodSource("trainingSearchFilterValidProvider")
    void shouldValidateTrainingSearchFilterIsValid(TrainingSearchFilter filter) {
        Set<ConstraintViolation<TrainingSearchFilter>> violations = validator.validate(filter);

        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("trainingSearchFilterInvalidProvider")
    void shouldValidateTrainingSearchFilterHasExpectedError(TrainingSearchFilter filter, String expectedMessage) {
        Set<ConstraintViolation<TrainingSearchFilter>> violations = validator.validate(filter);

        assertThat(violations)
                .isNotEmpty()
                .extracting(ConstraintViolation::getMessage)
                .contains(expectedMessage);
    }

    private static Stream<Arguments> traineeCreateDtoValidProvider() {
        return Stream.of(
                Arguments.of(TraineeCreateDto.builder().firstName("Liam").lastName("Miller").build())
        );
    }

    private static Stream<Arguments> traineeCreateDtoInvalidProvider() {
        return Stream.of(
                Arguments.of(TraineeCreateDto.builder().firstName("").lastName("Miller").build(), "Trainee first name is required"),
                Arguments.of(TraineeCreateDto.builder().firstName("Liam").lastName("").build(), "Trainee last name is required"),
                Arguments.of(TraineeCreateDto.builder().firstName("A".repeat(101)).lastName("Miller").build(), "Trainee first name must not exceed 100 characters"),
                Arguments.of(TraineeCreateDto.builder().firstName("Liam").lastName("A".repeat(101)).build(), "Trainee last name must not exceed 100 characters"),
                Arguments.of(TraineeCreateDto.builder().firstName("Liam").lastName("Miller").address("A".repeat(101)).build(), "Address must not exceed 100 characters"),
                Arguments.of(TraineeCreateDto.builder().firstName(null).lastName("Miller").build(), "Trainee first name is required"),
                Arguments.of(TraineeCreateDto.builder().firstName("Liam").lastName(null).build(), "Trainee last name is required")
        );
    }

    private static Stream<Arguments> trainerCreateDtoValidProvider() {
        return Stream.of(
                Arguments.of(TrainerCreateDto.builder().firstName("Liam").lastName("Miller").specializationId(1L).build())
        );
    }

    private static Stream<Arguments> trainerCreateDtoInvalidProvider() {
        return Stream.of(
                Arguments.of(TrainerCreateDto.builder().firstName("").lastName("Miller").specializationId(1L).build(), "Trainer first name is required"),
                Arguments.of(TrainerCreateDto.builder().firstName("Liam").lastName("").specializationId(1L).build(), "Trainer last name is required"),
                Arguments.of(TrainerCreateDto.builder().firstName("Liam").lastName("Miller").build(), "Specialization is required for trainer"),
                Arguments.of(TrainerCreateDto.builder().firstName("A".repeat(101)).lastName("Miller").specializationId(1L).build(), "Trainer first name must not exceed 100 characters"),
                Arguments.of(TrainerCreateDto.builder().firstName("Liam").lastName("A".repeat(101)).specializationId(1L).build(), "Trainer last name must not exceed 100 characters"),
                Arguments.of(TrainerCreateDto.builder().firstName(null).lastName("Miller").specializationId(1L).build(), "Trainer first name is required"),
                Arguments.of(TrainerCreateDto.builder().firstName("Liam").lastName(null).specializationId(1L).build(), "Trainer last name is required")
        );
    }

    private static Stream<Arguments> trainingCreateDtoValidProvider() {
        return Stream.of(
                Arguments.of(TrainingCreateDto.builder()
                        .traineeId(1L).trainerId(1L).trainingTypeId(1L)
                        .trainingName("Yoga").trainingDuration(60).trainingDate(LocalDate.now())
                        .build())
        );
    }

    private static Stream<Arguments> trainingCreateDtoInvalidProvider() {
        return Stream.of(
                Arguments.of(TrainingCreateDto.builder().trainingName("").build(), "Training name is required"),
                Arguments.of(TrainingCreateDto.builder().trainingDuration(0).build(), "Training duration must be a positive number"),
                Arguments.of(TrainingCreateDto.builder().trainingDate(null).build(), "Training date is required")
        );
    }

    private static Stream<Arguments> passwordUpdateDtoValidProvider() {
        return Stream.of(
                Arguments.of(PasswordUpdateDto.builder().password("SecurePass123").build())
        );
    }

    private static Stream<Arguments> passwordUpdateDtoInvalidProvider() {
        return Stream.of(
                Arguments.of(PasswordUpdateDto.builder().password("short").build(), "Password must be at least 10 characters long"),
                Arguments.of(PasswordUpdateDto.builder().password("").build(), "Password is required"),
                Arguments.of(PasswordUpdateDto.builder().password(null).build(), "Password is required")
        );
    }

    private static Stream<Arguments> trainingSearchFilterValidProvider() {
        return Stream.of(
                Arguments.of(TrainingSearchFilter.builder().username("user").build()),
                Arguments.of(TrainerTrainingSearchFilter.builder().username("user").build()),
                Arguments.of(TraineeTrainingSearchFilter.builder().username("user").build())
        );
    }

    private static Stream<Arguments> trainingSearchFilterInvalidProvider() {
        return Stream.of(
                Arguments.of(TrainingSearchFilter.builder().username("").build(), "Username is required for search"),
                Arguments.of(TrainingSearchFilter.builder().username(null).build(), "Username is required for search"),
                Arguments.of(TrainerTrainingSearchFilter.builder().username("").build(), "Username is required for search"),
                Arguments.of(TrainerTrainingSearchFilter.builder().username(null).build(), "Username is required for search"),
                Arguments.of(TraineeTrainingSearchFilter.builder().username("").build(), "Username is required for search"),
                Arguments.of(TraineeTrainingSearchFilter.builder().username(null).build(), "Username is required for search")
        );
    }

}