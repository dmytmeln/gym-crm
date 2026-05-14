package com.gym.crm.facade;

import com.gym.crm.GymCrmApplication;
import com.gym.crm.config.BaseDbIntegrationTest;
import com.gym.crm.dto.LoginRequestDto;
import com.gym.crm.dto.PasswordUpdateDto;
import com.gym.crm.dto.TraineeCreateDto;
import com.gym.crm.dto.TraineeCreateResponseDto;
import com.gym.crm.dto.TraineeResponseDto;
import com.gym.crm.dto.TraineeUpdateDto;
import com.gym.crm.dto.TrainerCreateDto;
import com.gym.crm.dto.TrainerCreateResponseDto;
import com.gym.crm.dto.TrainerResponseDto;
import com.gym.crm.dto.TrainerUpdateDto;
import com.gym.crm.dto.TrainingCreateDto;
import com.gym.crm.dto.TrainingResponseDto;
import com.gym.crm.dto.filter.TraineeTrainingSearchFilter;
import com.gym.crm.dto.filter.TrainerTrainingSearchFilter;
import com.gym.crm.exception.ValidationException;
import com.gym.crm.security.AuthenticationException;
import com.gym.crm.security.Role;
import com.gym.crm.security.SecurityContext;
import com.gym.crm.security.UserCredentials;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static com.gym.crm.factory.TraineeTestFactory.buildTraineeCreateDto;
import static com.gym.crm.factory.TraineeTestFactory.buildTraineeUpdateDto;
import static com.gym.crm.factory.TrainerTestFactory.buildTrainerCreateDto;
import static com.gym.crm.factory.TrainerTestFactory.buildTrainerUpdateDto;
import static com.gym.crm.factory.TrainingTestFactory.buildTrainingCreateDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(GymCrmApplication.class)
@Sql(scripts = {"classpath:datasets/cleanup-all.sql", "classpath:datasets/seed-data.sql"})
class GymFacadeIntegrationTest extends BaseDbIntegrationTest {

    private static final Long TRAINEE_ID = 1L;
    private static final String TRAINEE_USERNAME = "liam.miller";
    private static final String TRAINEE_PASSWORD = "pass123";
    private static final Long TRAINER_ID = 1L;
    private static final String TRAINER_USERNAME = "marcus.stone";
    private static final String TRAINER_PASSWORD = "pass111";
    private static final Long TRAINING_ID = 1L;
    private static final String TRAINING_NAME = "Morning HIIT";
    private static final int TRAINING_DURATION = 60;

    @Autowired
    private GymFacade facade;

    @AfterEach
    void logout() {
        SecurityContext.clear();
    }

    @Test
    void shouldGetTraineeById() {
        authenticateAsTrainee();
        TraineeResponseDto actual = facade.getTrainee(TRAINEE_USERNAME, TRAINEE_ID);

        assertNotNull(actual);
        assertEquals(TRAINEE_ID, actual.id());
        assertEquals(TRAINEE_USERNAME, actual.username());
        assertEquals("Liam", actual.firstName());
        assertEquals("Miller", actual.lastName());
        assertTrue(actual.active());
    }

    @Test
    void shouldGetTraineeByUsername() {
        authenticateAsTrainee();
        TraineeResponseDto actual = facade.getTraineeByUsername(TRAINEE_USERNAME);

        assertNotNull(actual);
        assertEquals(TRAINEE_ID, actual.id());
        assertEquals(TRAINEE_USERNAME, actual.username());
    }

    @Test
    void shouldGetAllTrainees() {
        authenticateAsTrainer();
        List<TraineeResponseDto> actual = facade.getAllTrainees(TRAINER_USERNAME);

        assertEquals(2, actual.size());
    }

    @Test
    void shouldCreateTrainee() {
        TraineeCreateDto dto = buildTraineeCreateDto();

        TraineeCreateResponseDto actual = facade.createTrainee(dto);

        assertNotNull(actual.id());
        assertNotNull(actual.username());
        assertNotNull(actual.password());
        assertEquals(dto.firstName(), actual.firstName());
        assertEquals(dto.lastName(), actual.lastName());
        assertEquals(dto.active(), actual.active());
        assertEquals(dto.address(), actual.address());
        assertEquals(dto.dateOfBirth(), actual.dateOfBirth());
    }

    @Test
    void shouldThrowValidationExceptionWhenCreatingTraineeWithInvalidDto() {
        TraineeCreateDto invalidDto = TraineeCreateDto.builder()
                .firstName("")
                .lastName("Doe")
                .active(true)
                .build();

        assertThrows(ValidationException.class, () -> facade.createTrainee(invalidDto));
    }

    @Test
    void shouldUpdateTrainee() {
        authenticateAsTrainee();
        TraineeUpdateDto updateDto = buildTraineeUpdateDto();

        TraineeResponseDto actual = facade.updateTrainee(TRAINEE_USERNAME, TRAINEE_ID, updateDto);

        assertEquals(TRAINEE_ID, actual.id());
        assertEquals(updateDto.firstName(), actual.firstName());
        assertEquals(updateDto.lastName(), actual.lastName());
        assertEquals(updateDto.active(), actual.active());
        assertEquals(updateDto.address(), actual.address());
        assertEquals(updateDto.dateOfBirth(), actual.dateOfBirth());
    }

    @Test
    void shouldUpdateTraineeTrainers() {
        authenticateAsTrainee();

        facade.updateTraineeTrainers(TRAINEE_USERNAME, TRAINEE_ID, List.of(2L, 3L));

        List<TrainerResponseDto> unassigned = facade.getAllTrainersNotAssignedToTrainee(TRAINEE_USERNAME);
        assertEquals(1, unassigned.size());
        assertEquals(TRAINER_ID, unassigned.get(0).id());
    }

    @Test
    void shouldUpdateTraineePassword() {
        authenticateAsTrainee();
        String newPassword = "newPass999";
        PasswordUpdateDto passwordDto = new PasswordUpdateDto(newPassword);

        facade.updateTraineePassword(TRAINEE_USERNAME, TRAINEE_ID, passwordDto);

        facade.logout();
        facade.login(new LoginRequestDto(TRAINEE_USERNAME, newPassword, Role.TRAINEE));
        assertNotNull(SecurityContext.getCurrentUser());
        assertEquals(TRAINEE_USERNAME, SecurityContext.getCurrentUser().username());
        facade.logout();
        assertThrows(AuthenticationException.class, () ->
                facade.login(new LoginRequestDto(TRAINEE_USERNAME, TRAINEE_PASSWORD, Role.TRAINEE)));
    }

    @Test
    void shouldActivateTrainee() {
        authenticateAsTrainee();
        facade.deactivateTrainee(TRAINEE_USERNAME, TRAINEE_ID);

        facade.activateTrainee(TRAINEE_USERNAME, TRAINEE_ID);

        assertTrue(facade.getTrainee(TRAINEE_USERNAME, TRAINEE_ID).active());
    }

    @Test
    void shouldDeactivateTrainee() {
        authenticateAsTrainee();
        facade.deactivateTrainee(TRAINEE_USERNAME, TRAINEE_ID);

        assertFalse(facade.getTrainee(TRAINEE_USERNAME, TRAINEE_ID).active());
    }

    @Test
    void shouldDeleteTrainee() {
        authenticateAsTrainee();
        boolean actual = facade.deleteTrainee(TRAINEE_USERNAME, 2L);

        assertTrue(actual);
    }

    @Test
    void shouldDeleteTraineeByUsername() {
        authenticateAsTrainee();
        boolean actual = facade.deleteTraineeByUsername(TRAINEE_USERNAME);

        assertTrue(actual);
    }

    @Test
    void shouldGetTrainerById() {
        authenticateAsTrainer();
        TrainerResponseDto actual = facade.getTrainer(TRAINER_USERNAME, TRAINER_ID);

        assertNotNull(actual);
        assertEquals(TRAINER_ID, actual.id());
        assertEquals(TRAINER_USERNAME, actual.username());
        assertEquals("Marcus", actual.firstName());
        assertEquals("Stone", actual.lastName());
        assertTrue(actual.active());
        assertEquals("CARDIO", actual.specializationName());
    }

    @Test
    void shouldGetTrainerByUsername() {
        authenticateAsTrainer();
        TrainerResponseDto actual = facade.getTrainerByUsername(TRAINER_USERNAME);

        assertNotNull(actual);
        assertEquals(TRAINER_ID, actual.id());
        assertEquals(TRAINER_USERNAME, actual.username());
    }

    @Test
    void shouldGetAllTrainers() {
        authenticateAsTrainee();
        List<TrainerResponseDto> actual = facade.getAllTrainers(TRAINEE_USERNAME);

        assertEquals(3, actual.size());
    }

    @Test
    void shouldGetAllTrainersNotAssignedToTrainee() {
        authenticateAsTrainee();
        List<Long> expectedTrainerIds = List.of(2L, 3L);

        List<TrainerResponseDto> actual = facade.getAllTrainersNotAssignedToTrainee(TRAINEE_USERNAME);

        assertThat(actual)
                .hasSize(2)
                .extracting(TrainerResponseDto::id)
                .containsAll(expectedTrainerIds);
    }

    @Test
    void shouldCreateTrainer() {
        TrainerCreateDto dto = buildTrainerCreateDto();

        TrainerCreateResponseDto actual = facade.createTrainer(dto);

        assertNotNull(actual.id());
        assertNotNull(actual.username());
        assertNotNull(actual.password());
        assertEquals(dto.firstName(), actual.firstName());
        assertEquals(dto.lastName(), actual.lastName());
        assertEquals(dto.active(), actual.active());
    }

    @Test
    void shouldUpdateTrainer() {
        authenticateAsTrainer();
        TrainerUpdateDto updateDto = buildTrainerUpdateDto();

        TrainerResponseDto actual = facade.updateTrainer(TRAINER_USERNAME, TRAINER_ID, updateDto);

        assertEquals(TRAINER_ID, actual.id());
        assertEquals(updateDto.firstName(), actual.firstName());
        assertEquals(updateDto.lastName(), actual.lastName());
        assertEquals(updateDto.active(), actual.active());
    }

    @Test
    void shouldUpdateTrainerPassword() {
        authenticateAsTrainer();
        String newPassword = "newTrainerPass";
        PasswordUpdateDto passwordDto = new PasswordUpdateDto(newPassword);

        facade.updateTrainerPassword(TRAINER_USERNAME, TRAINER_ID, passwordDto);

        facade.logout();
        facade.login(new LoginRequestDto(TRAINER_USERNAME, newPassword, Role.TRAINER));
        assertNotNull(SecurityContext.getCurrentUser());
        assertEquals(TRAINER_USERNAME, SecurityContext.getCurrentUser().username());
        facade.logout();
        assertThrows(AuthenticationException.class, () ->
                facade.login(new LoginRequestDto(TRAINER_USERNAME, TRAINER_PASSWORD, Role.TRAINER)));
    }

    @Test
    void shouldActivateTrainer() {
        authenticateAsTrainer();
        facade.deactivateTrainer(TRAINER_USERNAME, TRAINER_ID);

        facade.activateTrainer(TRAINER_USERNAME, TRAINER_ID);

        assertTrue(facade.getTrainer(TRAINER_USERNAME, TRAINER_ID).active());
    }

    @Test
    void shouldDeactivateTrainer() {
        authenticateAsTrainer();
        facade.deactivateTrainer(TRAINER_USERNAME, TRAINER_ID);

        assertFalse(facade.getTrainer(TRAINER_USERNAME, TRAINER_ID).active());
    }

    @Test
    void shouldCreateTraining() {
        authenticateAsTrainer();
        TrainingCreateDto dto = buildTrainingCreateDto(TRAINEE_ID, TRAINER_ID);

        TrainingResponseDto actual = facade.createTraining(TRAINER_USERNAME, dto);

        assertNotNull(actual.id());
        assertEquals(dto.traineeId(), actual.traineeId());
        assertEquals(dto.trainerId(), actual.trainerId());
        assertEquals(dto.trainingName(), actual.trainingName());
        assertEquals(dto.trainingDuration(), actual.trainingDuration());
        assertEquals(dto.trainingDate(), actual.trainingDate());
    }

    @Test
    void shouldGetTrainingById() {
        authenticateAsTrainer();
        TrainingResponseDto actual = facade.getTraining(TRAINER_USERNAME, TRAINING_ID);

        assertNotNull(actual);
        assertEquals(TRAINING_ID, actual.id());
        assertEquals(TRAINEE_ID, actual.traineeId());
        assertEquals(TRAINER_ID, actual.trainerId());
        assertEquals(TRAINING_NAME, actual.trainingName());
        assertEquals(TRAINING_DURATION, actual.trainingDuration());
    }

    @Test
    void shouldGetAllTrainings() {
        authenticateAsTrainer();
        List<TrainingResponseDto> actual = facade.getAllTrainings(TRAINER_USERNAME);

        assertEquals(2, actual.size());
    }

    @Test
    void shouldGetTraineeTrainings() {
        authenticateAsTrainee();
        TraineeTrainingSearchFilter filter = TraineeTrainingSearchFilter.builder()
                .username(TRAINEE_USERNAME)
                .build();

        List<TrainingResponseDto> actual = facade.getTrainingsByTraineeCriteria(TRAINEE_USERNAME, filter);

        assertEquals(1, actual.size());
        assertEquals(TRAINING_NAME, actual.get(0).trainingName());
    }

    @Test
    void shouldGetEmptyTraineeTrainingsWhenFilterDoesNotMatch() {
        authenticateAsTrainee();
        TraineeTrainingSearchFilter filter = TraineeTrainingSearchFilter.builder()
                .username(TRAINEE_USERNAME)
                .trainerName("non.existent")
                .build();

        List<TrainingResponseDto> actual = facade.getTrainingsByTraineeCriteria(TRAINEE_USERNAME, filter);

        assertTrue(actual.isEmpty());
    }

    @Test
    void shouldGetTrainerTrainings() {
        authenticateAsTrainer();
        TrainerTrainingSearchFilter filter = TrainerTrainingSearchFilter.builder()
                .username(TRAINER_USERNAME)
                .build();

        List<TrainingResponseDto> actual = facade.getTrainingsByTrainerCriteria(TRAINER_USERNAME, filter);

        assertEquals(1, actual.size());
        assertEquals(TRAINING_NAME, actual.get(0).trainingName());
    }

    @Test
    void shouldGetEmptyTrainerTrainingsWhenFilterDoesNotMatch() {
        authenticateAsTrainer();
        TrainerTrainingSearchFilter filter = TrainerTrainingSearchFilter.builder()
                .username(TRAINER_USERNAME)
                .traineeName("non.existent")
                .build();

        List<TrainingResponseDto> actual = facade.getTrainingsByTrainerCriteria(TRAINER_USERNAME, filter);

        assertTrue(actual.isEmpty());
    }

    private void authenticateAsTrainee() {
        SecurityContext.setCurrentUser(new UserCredentials(TRAINEE_USERNAME, Role.TRAINEE));
    }

    private void authenticateAsTrainer() {
        SecurityContext.setCurrentUser(new UserCredentials(TRAINER_USERNAME, Role.TRAINER));
    }

}
