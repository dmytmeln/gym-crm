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
    void shouldValidateTraineeCreateDto(TraineeCreateDto dto, boolean isValid) {
        Set<ConstraintViolation<TraineeCreateDto>> violations = validator.validate(dto);
        assertThat(violations.isEmpty()).isEqualTo(isValid);
    }

    static Stream<Arguments> traineeCreateDtoProvider() {
        return Stream.of(
                Arguments.of(TraineeCreateDto.builder().firstName("Liam").lastName("Miller").build(), true),
                Arguments.of(TraineeCreateDto.builder().firstName("").lastName("Miller").build(), false),
                Arguments.of(TraineeCreateDto.builder().firstName("Liam").lastName("").build(), false),
                Arguments.of(TraineeCreateDto.builder().firstName("A".repeat(101)).lastName("Miller").build(), false),
                Arguments.of(TraineeCreateDto.builder().firstName("Liam").lastName("A".repeat(101)).build(), false),
                Arguments.of(TraineeCreateDto.builder().firstName("Liam").lastName("Miller").address("A".repeat(101)).build(), false),
                Arguments.of(TraineeCreateDto.builder().firstName(null).lastName("Miller").build(), false),
                Arguments.of(TraineeCreateDto.builder().firstName("Liam").lastName(null).build(), false)
        );
    }

    @ParameterizedTest
    @MethodSource("trainerCreateDtoProvider")
    void shouldValidateTrainerCreateDto(TrainerCreateDto dto, boolean isValid) {
        Set<ConstraintViolation<TrainerCreateDto>> violations = validator.validate(dto);
        assertThat(violations.isEmpty()).isEqualTo(isValid);
    }

    static Stream<Arguments> trainerCreateDtoProvider() {
        return Stream.of(
                Arguments.of(TrainerCreateDto.builder().firstName("Liam").lastName("Miller").specializationId(1L).build(), true),
                Arguments.of(TrainerCreateDto.builder().firstName("").lastName("Miller").specializationId(1L).build(), false),
                Arguments.of(TrainerCreateDto.builder().firstName("Liam").lastName("").specializationId(1L).build(), false),
                Arguments.of(TrainerCreateDto.builder().firstName("Liam").lastName("Miller").build(), false),
                Arguments.of(TrainerCreateDto.builder().firstName("A".repeat(101)).lastName("Miller").specializationId(1L).build(), false),
                Arguments.of(TrainerCreateDto.builder().firstName("Liam").lastName("A".repeat(101)).specializationId(1L).build(), false),
                Arguments.of(TrainerCreateDto.builder().firstName(null).lastName("Miller").specializationId(1L).build(), false),
                Arguments.of(TrainerCreateDto.builder().firstName("Liam").lastName(null).specializationId(1L).build(), false)
        );
    }

    @ParameterizedTest
    @MethodSource("trainingCreateDtoProvider")
    void shouldValidateTrainingCreateDto(TrainingCreateDto dto, boolean isValid) {
        Set<ConstraintViolation<TrainingCreateDto>> violations = validator.validate(dto);
        assertThat(violations.isEmpty()).isEqualTo(isValid);
    }

    static Stream<Arguments> trainingCreateDtoProvider() {
        return Stream.of(
                Arguments.of(TrainingCreateDto.builder()
                        .traineeId(1L).trainerId(1L).trainingTypeId(1L)
                        .trainingName("Yoga").trainingDuration(60).trainingDate(LocalDate.now())
                        .build(), true),
                Arguments.of(TrainingCreateDto.builder().trainingName("").build(), false),
                Arguments.of(TrainingCreateDto.builder().trainingDuration(0).build(), false),
                Arguments.of(TrainingCreateDto.builder().trainingDate(null).build(), false)
        );
    }

    @ParameterizedTest
    @MethodSource("passwordUpdateDtoProvider")
    void shouldValidatePasswordUpdateDto(PasswordUpdateDto dto, boolean isValid) {
        Set<ConstraintViolation<PasswordUpdateDto>> violations = validator.validate(dto);
        assertThat(violations.isEmpty()).isEqualTo(isValid);
    }

    static Stream<Arguments> passwordUpdateDtoProvider() {
        return Stream.of(
                Arguments.of(PasswordUpdateDto.builder().password("SecurePass123").build(), true),
                Arguments.of(PasswordUpdateDto.builder().password("short").build(), false),
                Arguments.of(PasswordUpdateDto.builder().password("").build(), false),
                Arguments.of(PasswordUpdateDto.builder().password(null).build(), false)
        );
    }

    @ParameterizedTest
    @MethodSource("trainingSearchFilterProvider")
    void shouldValidateTrainingSearchFilter(TrainingSearchFilter filter, boolean isValid) {
        Set<ConstraintViolation<TrainingSearchFilter>> violations = validator.validate(filter);
        assertThat(violations.isEmpty()).isEqualTo(isValid);
    }

    static Stream<Arguments> trainingSearchFilterProvider() {
        return Stream.of(
                Arguments.of(TrainingSearchFilter.builder().username("user").build(), true),
                Arguments.of(TrainingSearchFilter.builder().username("").build(), false),
                Arguments.of(TrainingSearchFilter.builder().username(null).build(), false),
                Arguments.of(TrainerTrainingSearchFilter.builder().username("user").build(), true),
                Arguments.of(TrainerTrainingSearchFilter.builder().username("").build(), false),
                Arguments.of(TrainerTrainingSearchFilter.builder().username(null).build(), false),
                Arguments.of(TraineeTrainingSearchFilter.builder().username("user").build(), true),
                Arguments.of(TraineeTrainingSearchFilter.builder().username("").build(), false),
                Arguments.of(TraineeTrainingSearchFilter.builder().username(null).build(), false)
        );
    }

}
