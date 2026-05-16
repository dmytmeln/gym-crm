package com.gym.crm.facade;

import com.gia.openapi.model.ActivationStatusRequest;
import com.gia.openapi.model.AssignedTrainerResponse;
import com.gia.openapi.model.GetTraineeTrainingResponse;
import com.gia.openapi.model.GetTrainerTrainingResponse;
import com.gia.openapi.model.LoginChangeRequest;
import com.gia.openapi.model.LoginRequest;
import com.gia.openapi.model.TraineeAssignedTrainersUpdateRequest;
import com.gia.openapi.model.TraineeCreateRequest;
import com.gia.openapi.model.TraineeCreateResponse;
import com.gia.openapi.model.TraineeGetResponse;
import com.gia.openapi.model.TraineeUpdateRequest;
import com.gia.openapi.model.TraineeUpdateResponse;
import com.gia.openapi.model.TrainerCreateRequest;
import com.gia.openapi.model.TrainerCreateResponse;
import com.gia.openapi.model.TrainerGetResponse;
import com.gia.openapi.model.TrainerUpdateRequest;
import com.gia.openapi.model.TrainerUpdateResponse;
import com.gym.crm.GymCrmApplication;
import com.gym.crm.config.BaseDbIntegrationTest;
import com.gym.crm.dto.TrainingCreateDto;
import com.gym.crm.dto.TrainingResponseDto;
import com.gym.crm.dto.filter.TraineeTrainingSearchFilter;
import com.gym.crm.dto.filter.TrainerTrainingSearchFilter;
import com.gym.crm.security.AuthenticationException;
import com.gym.crm.security.Role;
import com.gym.crm.security.SecurityContext;
import com.gym.crm.security.UserCredentials;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.util.List;

import static com.gym.crm.factory.TrainingTestFactory.buildTrainingCreateDto;
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
    private static final String TRAINEE_PASSWORD = "password123";
    private static final Long TRAINER_ID = 1L;
    private static final String TRAINER_USERNAME = "marcus.stone";
    private static final Long TRAINING_ID = 1L;
    private static final String TRAINING_NAME = "Morning HIIT";
    private static final int TRAINING_DURATION = 60;

    @Autowired
    private GymFacade facade;

    @AfterEach
    void tearDown() {
        SecurityContext.clear();
    }

    @Test
    void shouldGetTraineeByUsername() {
        authenticateAsTrainee();

        TraineeGetResponse actual = facade.getTraineeByUsername(TRAINEE_USERNAME);

        assertNotNull(actual);
        assertEquals("Liam", actual.getFirstName());
        assertEquals("Miller", actual.getLastName());
        assertTrue(actual.getIsActive());
    }

    @Test
    void shouldGetAvailableTrainersForTrainee() {
        authenticateAsTrainee();

        List<AssignedTrainerResponse> actual = facade.getAvailableTrainersForTrainee(TRAINEE_USERNAME);

        assertEquals(2, actual.size());
    }

    @Test
    void shouldGetTraineeTrainings() {
        authenticateAsTrainee();
        TraineeTrainingSearchFilter filter = TraineeTrainingSearchFilter.builder()
                .username(TRAINEE_USERNAME)
                .build();

        List<GetTraineeTrainingResponse> actual = facade.getTraineeTrainings(TRAINEE_USERNAME, filter);

        assertEquals(1, actual.size());
        assertEquals("Morning HIIT", actual.get(0).getTrainingName());
    }

    @Test
    void shouldCreateTrainee() {
        TraineeCreateRequest request = new TraineeCreateRequest("John", "Doe")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("123 Test St");

        TraineeCreateResponse actual = facade.createTrainee(request);

        assertNotNull(actual.getUsername());
        assertNotNull(actual.getPassword());
    }

    @Test
    void shouldUpdateTrainee() {
        authenticateAsTrainee();
        TraineeUpdateRequest request = new TraineeUpdateRequest("Updated", "Name", true)
                .address("Updated Address")
                .dateOfBirth(LocalDate.of(1995, 5, 15));

        TraineeUpdateResponse actual = facade.updateTrainee(TRAINEE_USERNAME, request);

        assertEquals("Updated", actual.getFirstName());
        assertEquals("Name", actual.getLastName());
        assertTrue(actual.getIsActive());
        assertEquals("Updated Address", actual.getAddress());
    }

    @Test
    void shouldUpdateTraineeTrainers() {
        authenticateAsTrainee();
        TraineeAssignedTrainersUpdateRequest request = new TraineeAssignedTrainersUpdateRequest()
                .trainerUsernames(List.of("sarah.adams", "alex.morgan"));

        facade.updateTraineeTrainers(TRAINEE_USERNAME, request);

        TraineeGetResponse trainee = facade.getTraineeByUsername(TRAINEE_USERNAME);
        assertEquals(2, trainee.getTrainers().size());
    }

    @Test
    void shouldUpdateTraineeActivationStatus() {
        authenticateAsTrainee();
        ActivationStatusRequest request = new ActivationStatusRequest(false);

        facade.updateTraineeActivationStatus(TRAINEE_USERNAME, request);

        TraineeGetResponse trainee = facade.getTraineeByUsername(TRAINEE_USERNAME);
        assertFalse(trainee.getIsActive());
    }

    @Test
    void shouldUpdateTraineePassword() {
        authenticateAsTrainee();
        String newPassword = "newPass999";
        LoginChangeRequest request = new LoginChangeRequest()
                .username(TRAINEE_USERNAME)
                .oldPassword(TRAINEE_PASSWORD)
                .newPassword(newPassword);

        facade.changePassword(TRAINEE_USERNAME, request);

        SecurityContext.clear();
        facade.login(new LoginRequest().username(TRAINEE_USERNAME).password(newPassword));
        assertNotNull(SecurityContext.getCurrentUser());
        assertEquals(TRAINEE_USERNAME, SecurityContext.getCurrentUser().username());
        SecurityContext.clear();
        assertThrows(AuthenticationException.class, () ->
                facade.login(new LoginRequest().username(TRAINEE_USERNAME).password(TRAINEE_PASSWORD)));
    }

    @Test
    void shouldDeleteTraineeByUsername() {
        authenticateAsTrainee();

        boolean actual = facade.deleteTraineeByUsername(TRAINEE_USERNAME);

        assertTrue(actual);
    }

    @Test
    void shouldGetTrainerByUsername() {
        authenticateAsTrainer();

        TrainerGetResponse actual = facade.getTrainerByUsername(TRAINER_USERNAME);

        assertNotNull(actual);
        assertEquals("Marcus", actual.getFirstName());
        assertEquals("Stone", actual.getLastName());
    }

    @Test
    void shouldCreateTrainer() {
        TrainerCreateRequest request = new TrainerCreateRequest()
                .firstName("John")
                .lastName("Doe")
                .specialization("CARDIO");

        TrainerCreateResponse actual = facade.createTrainer(request);

        assertNotNull(actual.getUsername());
        assertNotNull(actual.getPassword());
    }

    @Test
    void shouldUpdateTrainer() {
        authenticateAsTrainer();
        TrainerUpdateRequest request = new TrainerUpdateRequest()
                .firstName("Updated")
                .lastName("Name")
                .isActive(false);

        TrainerUpdateResponse actual = facade.updateTrainer(TRAINER_USERNAME, request);

        assertEquals("Updated", actual.getFirstName());
        assertEquals("Name", actual.getLastName());
        assertFalse(actual.getIsActive());
    }

    @Test
    void shouldUpdateTrainerActivationStatus() {
        authenticateAsTrainer();
        facade.updateTrainerActivationStatus(TRAINER_USERNAME, false);

        facade.updateTrainerActivationStatus(TRAINER_USERNAME, true);

        TrainerGetResponse trainer = facade.getTrainerByUsername(TRAINER_USERNAME);
        assertNotNull(trainer);
        assertTrue(trainer.getIsActive());
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
    void shouldGetTrainerTrainings() {
        authenticateAsTrainer();
        TrainerTrainingSearchFilter filter = TrainerTrainingSearchFilter.builder()
                .username(TRAINER_USERNAME)
                .build();

        List<GetTrainerTrainingResponse> actual = facade.getTrainerTrainings(TRAINER_USERNAME, filter);

        assertEquals(1, actual.size());
        assertEquals(TRAINING_NAME, actual.get(0).getTrainingName());
    }

    @Test
    void shouldGetEmptyTrainerTrainingsWhenFilterDoesNotMatch() {
        authenticateAsTrainer();
        TrainerTrainingSearchFilter filter = TrainerTrainingSearchFilter.builder()
                .username(TRAINER_USERNAME)
                .traineeName("non.existent")
                .build();

        List<GetTrainerTrainingResponse> actual = facade.getTrainerTrainings(TRAINER_USERNAME, filter);

        assertTrue(actual.isEmpty());
    }

    private void authenticateAsTrainee() {
        SecurityContext.setCurrentUser(new UserCredentials(TRAINEE_USERNAME, Role.TRAINEE));
    }

    private void authenticateAsTrainer() {
        SecurityContext.setCurrentUser(new UserCredentials(TRAINER_USERNAME, Role.TRAINER));
    }

}
