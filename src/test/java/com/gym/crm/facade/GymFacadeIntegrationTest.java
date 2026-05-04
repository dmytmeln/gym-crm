package com.gym.crm.facade;

import com.gym.crm.GymCrmApplication;
import com.gym.crm.config.BaseDbIntegrationTest;
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

    @Test
    void shouldGetTraineeById() {
        TraineeResponseDto actual = facade.getTrainee(TRAINEE_ID);

        assertNotNull(actual);
        assertEquals(TRAINEE_ID, actual.id());
        assertEquals(TRAINEE_USERNAME, actual.username());
        assertEquals("Liam", actual.firstName());
        assertEquals("Miller", actual.lastName());
        assertTrue(actual.active());
    }

    @Test
    void shouldGetTraineeByUsername() {
        TraineeResponseDto actual = facade.getTraineeByUsername(TRAINEE_USERNAME);

        assertNotNull(actual);
        assertEquals(TRAINEE_ID, actual.id());
        assertEquals(TRAINEE_USERNAME, actual.username());
    }

    @Test
    void shouldGetAllTrainees() {
        List<TraineeResponseDto> actual = facade.getAllTrainees();

        assertEquals(2, actual.size());
    }

    @Test
    void shouldDoesTraineeUsernameAndPasswordMatch() {
        boolean actual = facade.doesTraineeUsernameAndPasswordMatch(TRAINEE_USERNAME, TRAINEE_PASSWORD);

        assertTrue(actual);
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
    void shouldUpdateTrainee() {
        TraineeUpdateDto updateDto = buildTraineeUpdateDto();

        TraineeResponseDto actual = facade.updateTrainee(TRAINEE_ID, updateDto);

        assertEquals(TRAINEE_ID, actual.id());
        assertEquals(updateDto.firstName(), actual.firstName());
        assertEquals(updateDto.lastName(), actual.lastName());
        assertEquals(updateDto.active(), actual.active());
        assertEquals(updateDto.address(), actual.address());
        assertEquals(updateDto.dateOfBirth(), actual.dateOfBirth());
    }

    @Test
    void shouldUpdateTraineeTrainers() {
        facade.updateTraineeTrainers(TRAINEE_ID, List.of(2L, 3L));

        List<TrainerResponseDto> unassigned = facade.getAllTrainersNotAssignedToTrainee(TRAINEE_USERNAME);
        assertEquals(1, unassigned.size());
        assertEquals(TRAINER_ID, unassigned.get(0).id());
    }

    @Test
    void shouldUpdateTraineePassword() {
        String newPassword = "newPass999";

        facade.updateTraineePassword(TRAINEE_ID, newPassword);

        assertTrue(facade.doesTraineeUsernameAndPasswordMatch(TRAINEE_USERNAME, newPassword));
    }

    @Test
    void shouldActivateTrainee() {
        facade.deactivateTrainee(TRAINEE_ID);
        facade.activateTrainee(TRAINEE_ID);

        assertTrue(facade.getTrainee(TRAINEE_ID).active());
    }

    @Test
    void shouldDeactivateTrainee() {
        facade.deactivateTrainee(TRAINEE_ID);

        assertFalse(facade.getTrainee(TRAINEE_ID).active());
    }

    @Test
    void shouldDeleteTrainee() {
        boolean actual = facade.deleteTrainee(2L);

        assertTrue(actual);
    }

    @Test
    void shouldDeleteTraineeByUsername() {
        boolean actual = facade.deleteTraineeByUsername("liam.miller");

        assertTrue(actual);
    }

    @Test
    void shouldGetTrainerById() {
        TrainerResponseDto actual = facade.getTrainer(TRAINER_ID);

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
        TrainerResponseDto actual = facade.getTrainerByUsername(TRAINER_USERNAME);

        assertNotNull(actual);
        assertEquals(TRAINER_ID, actual.id());
        assertEquals(TRAINER_USERNAME, actual.username());
    }

    @Test
    void shouldGetAllTrainers() {
        List<TrainerResponseDto> actual = facade.getAllTrainers();

        assertEquals(3, actual.size());
    }

    @Test
    void shouldGetAllTrainersNotAssignedToTrainee() {
        List<Long> expectedTrainerIds = List.of(2L, 3L);

        List<TrainerResponseDto> actual = facade.getAllTrainersNotAssignedToTrainee(TRAINEE_USERNAME);

        assertThat(actual)
                .hasSize(2)
                .extracting(TrainerResponseDto::id)
                .containsAll(expectedTrainerIds);
    }

    @Test
    void shouldDoesTrainerUsernameAndPasswordMatch() {
        boolean actual = facade.doesTrainerUsernameAndPasswordMatch(TRAINER_USERNAME, TRAINER_PASSWORD);

        assertTrue(actual);
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
        TrainerUpdateDto updateDto = buildTrainerUpdateDto();

        TrainerResponseDto actual = facade.updateTrainer(TRAINER_ID, updateDto);

        assertEquals(TRAINER_ID, actual.id());
        assertEquals(updateDto.firstName(), actual.firstName());
        assertEquals(updateDto.lastName(), actual.lastName());
        assertEquals(updateDto.active(), actual.active());
    }

    @Test
    void shouldUpdateTrainerPassword() {
        String newPassword = "newTrainerPass";

        facade.updateTrainerPassword(TRAINER_ID, newPassword);

        assertTrue(facade.doesTrainerUsernameAndPasswordMatch(TRAINER_USERNAME, newPassword));
    }

    @Test
    void shouldActivateTrainer() {
        facade.deactivateTrainer(TRAINER_ID);
        facade.activateTrainer(TRAINER_ID);

        assertTrue(facade.getTrainer(TRAINER_ID).active());
    }

    @Test
    void shouldDeactivateTrainer() {
        facade.deactivateTrainer(TRAINER_ID);

        assertFalse(facade.getTrainer(TRAINER_ID).active());
    }

    @Test
    void shouldCreateTraining() {
        TrainingCreateDto dto = buildTrainingCreateDto(TRAINEE_ID, TRAINER_ID);

        TrainingResponseDto actual = facade.createTraining(dto);

        assertNotNull(actual.id());
        assertEquals(dto.traineeId(), actual.traineeId());
        assertEquals(dto.trainerId(), actual.trainerId());
        assertEquals(dto.trainingName(), actual.trainingName());
        assertEquals(dto.trainingDuration(), actual.trainingDuration());
        assertEquals(dto.trainingDate(), actual.trainingDate());
    }

    @Test
    void shouldGetTrainingById() {
        TrainingResponseDto actual = facade.getTraining(TRAINING_ID);

        assertNotNull(actual);
        assertEquals(TRAINING_ID, actual.id());
        assertEquals(TRAINEE_ID, actual.traineeId());
        assertEquals(TRAINER_ID, actual.trainerId());
        assertEquals(TRAINING_NAME, actual.trainingName());
        assertEquals(TRAINING_DURATION, actual.trainingDuration());
    }

    @Test
    void shouldGetAllTrainings() {
        List<TrainingResponseDto> actual = facade.getAllTrainings();

        assertEquals(2, actual.size());
    }

}
