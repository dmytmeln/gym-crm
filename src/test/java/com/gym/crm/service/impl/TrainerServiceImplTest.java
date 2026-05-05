package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.dao.TrainingTypeDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.TrainingType;
import com.gym.crm.entity.User;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.common.ProfileCredentialGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.gym.crm.factory.TraineeTestFactory.buildTraineeWithId;

import static com.gym.crm.factory.TrainerTestFactory.DEFAULT_FIRST_NAME;
import static com.gym.crm.factory.TrainerTestFactory.DEFAULT_LAST_NAME;
import static com.gym.crm.factory.TrainerTestFactory.DEFAULT_PASSWORD;
import static com.gym.crm.factory.TrainerTestFactory.DEFAULT_SPECIALIZATION_ID;
import static com.gym.crm.factory.TrainerTestFactory.DEFAULT_TRAINER_ID;
import static com.gym.crm.factory.TrainerTestFactory.DEFAULT_USERNAME;
import static com.gym.crm.factory.TrainerTestFactory.SECONDARY_TRAINER_ID;
import static com.gym.crm.factory.TrainerTestFactory.buildTrainerWithId;
import static com.gym.crm.factory.TrainerTestFactory.buildTrainerWithoutCredentials;
import static com.gym.crm.factory.TrainerTestFactory.getDefaultTrainerBuilder;
import static com.gym.crm.factory.TrainerTestFactory.getDefaultTrainingTypeBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    @Mock
    private TrainerDao dao;

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainingTypeDao trainingTypeDao;

    @Mock
    private ProfileCredentialGenerator generator;

    private TrainerService service;

    @BeforeEach
    void setUp() {
        TrainerServiceImpl implementation = new TrainerServiceImpl();
        implementation.setTrainerDao(dao);
        implementation.setTraineeDao(traineeDao);
        implementation.setTrainingTypeDao(trainingTypeDao);
        implementation.setCredentialGenerator(generator);
        service = implementation;
    }

    @Test
    void shouldCreateTrainerWithGeneratedCredentials() {
        Trainer trainerWithoutCredentials = buildTrainerWithoutCredentials();
        Trainer expected = buildTrainerWithId(DEFAULT_TRAINER_ID);

        when(trainingTypeDao.findById(DEFAULT_SPECIALIZATION_ID)).thenReturn(Optional.of(trainerWithoutCredentials.getSpecialization()));
        when(generator.generateUsername(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME)).thenReturn(DEFAULT_USERNAME);
        when(generator.generatePassword()).thenReturn(DEFAULT_PASSWORD);
        when(dao.save(any(Trainer.class))).thenReturn(expected);

        Trainer actual = service.createTrainer(trainerWithoutCredentials);

        verify(generator).generateUsername(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME);
        verify(generator).generatePassword();
        ArgumentCaptor<Trainer> trainerCaptor = ArgumentCaptor.forClass(Trainer.class);
        verify(dao).save(trainerCaptor.capture());
        Trainer trainerWithCredentials = trainerCaptor.getValue();
        assertEquals(DEFAULT_USERNAME, trainerWithCredentials.getUser().getUsername());
        assertEquals(DEFAULT_PASSWORD, trainerWithCredentials.getUser().getPassword());
        assertEquals(DEFAULT_FIRST_NAME, trainerWithCredentials.getUser().getFirstName());
        assertEquals(DEFAULT_LAST_NAME, trainerWithCredentials.getUser().getLastName());
        assertEquals(expected.getSpecialization(), trainerWithCredentials.getSpecialization());
        assertEquals(expected, actual);
    }

    @Test
    void shouldThrowNullPointerWhenCreatingNullTrainer() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.createTrainer(null));

        assertEquals("Trainer cannot be null", exception.getMessage());
        verifyNoInteractions(dao, generator);
    }

    @Test
    void shouldThrowExceptionWhenCreatingTrainerWithInvalidSpecialization() {
        Trainer trainer = buildTrainerWithoutCredentials();

        when(trainingTypeDao.findById(DEFAULT_SPECIALIZATION_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.createTrainer(trainer));

        assertEquals("TrainingType not found with id: " + DEFAULT_SPECIALIZATION_ID, exception.getMessage());
        verify(trainingTypeDao).findById(DEFAULT_SPECIALIZATION_ID);
        verifyNoInteractions(generator, dao);
    }

    @Test
    void shouldThrowNullPointerWhenCreatingTrainerWithNullUser() {
        Trainer trainer = Trainer.builder().build();

        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.createTrainer(trainer));

        assertEquals("Trainer user cannot be null", exception.getMessage());
        verifyNoInteractions(trainingTypeDao, generator, dao);
    }

    @Test
    void shouldReturnTrainerWhenGettingExistingTrainer() {
        Trainer expected = buildTrainerWithId();

        when(dao.findById(DEFAULT_TRAINER_ID)).thenReturn(Optional.of(expected));

        Trainer actual = service.getTrainer(DEFAULT_TRAINER_ID);

        assertEquals(expected, actual);
        verify(dao).findById(DEFAULT_TRAINER_ID);
    }

    @Test
    void shouldThrowEntityNotFoundWhenGettingUnknownTrainer() {
        when(dao.findById(DEFAULT_TRAINER_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.getTrainer(DEFAULT_TRAINER_ID));

        assertEquals("Trainer not found with id: " + DEFAULT_TRAINER_ID, exception.getMessage());
        verify(dao).findById(DEFAULT_TRAINER_ID);
    }

    @Test
    void shouldThrowNullPointerWhenGettingNullTrainerId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.getTrainer(null));

        assertEquals("Trainer ID cannot be null", exception.getMessage());
        verifyNoInteractions(dao, generator);
    }

    @Test
    void shouldReturnTrainerByUsername() {
        Trainer expected = buildTrainerWithId();

        when(dao.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(expected));

        Trainer actual = service.getTrainerByUsername(DEFAULT_USERNAME);

        assertEquals(expected, actual);
        verify(dao).findByUsername(DEFAULT_USERNAME);
    }

    @Test
    void shouldThrowExceptionWhenGettingUnknownTrainerByUsername() {
        when(dao.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.getTrainerByUsername(DEFAULT_USERNAME));

        assertEquals("Trainer not found with username: " + DEFAULT_USERNAME, exception.getMessage());
    }

    @Test
    void shouldReturnAllTrainers() {
        Trainer trainer1 = buildTrainerWithId(DEFAULT_TRAINER_ID);
        Trainer trainer2 = buildTrainerWithId(SECONDARY_TRAINER_ID);
        List<Trainer> expected = List.of(trainer1, trainer2);

        when(dao.findAll()).thenReturn(expected);

        List<Trainer> actual = service.getAllTrainers();

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void shouldReturnEmptyTrainers() {
        List<Trainer> expected = List.of();

        when(dao.findAll()).thenReturn(expected);

        List<Trainer> actual = service.getAllTrainers();

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void shouldReturnTrainersNotAssignedToTrainee() {
        String traineeUsername = "traineeUser";
        Trainee trainee = buildTraineeWithId();
        Trainer trainer = buildTrainerWithId();
        List<Trainer> expected = List.of(trainer);

        when(traineeDao.findByUsername(traineeUsername)).thenReturn(Optional.of(trainee));
        when(dao.findAllNotAssignedToTrainee(traineeUsername)).thenReturn(expected);

        List<Trainer> actual = service.getAllTrainersNotAssignedToTrainee(traineeUsername);

        assertEquals(expected, actual);
        verify(traineeDao).findByUsername(traineeUsername);
        verify(dao).findAllNotAssignedToTrainee(traineeUsername);
    }

    @Test
    void shouldThrowExceptionWhenGettingTrainersForUnknownTrainee() {
        String nonExistingTraineeUsername = "unknown";

        when(traineeDao.findByUsername(nonExistingTraineeUsername)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> service.getAllTrainersNotAssignedToTrainee(nonExistingTraineeUsername));

        assertEquals("Trainee not found with username: unknown", exception.getMessage());
        verify(traineeDao).findByUsername(nonExistingTraineeUsername);
        verifyNoInteractions(dao);
    }

    @Test
    void shouldThrowNullPointerWhenGettingTrainersForNullTrainee() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.getAllTrainersNotAssignedToTrainee(null));

        assertEquals("Trainee username cannot be null", exception.getMessage());
    }

    @Test
    void shouldReturnTrueWhenUsernameAndPasswordMatch() {
        Trainer trainer = buildTrainerWithId(DEFAULT_TRAINER_ID);

        when(dao.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(trainer));

        boolean result = service.doesUsernameAndPasswordMatch(DEFAULT_USERNAME, DEFAULT_PASSWORD);

        assertTrue(result);
        verify(dao).findByUsername(DEFAULT_USERNAME);
    }

    @Test
    void shouldReturnFalseWhenPasswordDoesNotMatch() {
        Trainer trainer = buildTrainerWithId(DEFAULT_TRAINER_ID);

        when(dao.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(trainer));

        boolean result = service.doesUsernameAndPasswordMatch(DEFAULT_USERNAME, "wrong");

        assertFalse(result);
        verify(dao).findByUsername(DEFAULT_USERNAME);
    }

    @Test
    void shouldThrowNullPointerWhenMatchingWithNullPassword() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.doesUsernameAndPasswordMatch(DEFAULT_USERNAME, null));

        assertEquals("Password cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowEntityNotFoundWhenNoTrainerExists() {
        when(dao.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.doesUsernameAndPasswordMatch(DEFAULT_USERNAME, DEFAULT_PASSWORD));

        assertEquals("Trainer not found with username: " + DEFAULT_USERNAME, exception.getMessage());
    }

    @Test
    void shouldThrowNullPointerWhenMatchingWithNullUsername() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.doesUsernameAndPasswordMatch(null, DEFAULT_PASSWORD));

        assertEquals("Trainer username cannot be null", exception.getMessage());
    }

    @Test
    void shouldMergeUpdatedFieldsAndPreserveExistingUsernameWhenUpdatingTrainer() {
        Trainer existingTrainer = buildTrainerWithId(DEFAULT_TRAINER_ID);
        Trainer updateRequest = getDefaultTrainerBuilder()
                .id(DEFAULT_TRAINER_ID)
                .user(getDefaultTrainerBuilder().build().getUser().toBuilder()
                        .firstName("Elena")
                        .lastName("Rodriguez")
                        .password("newPassword")
                        .isActive(false)
                        .build())
                .specialization(getDefaultTrainingTypeBuilder().id(2L).trainingTypeName("Strength").build())
                .build();
        Trainer expected = getDefaultTrainerBuilder()
                .id(DEFAULT_TRAINER_ID)
                .user(getDefaultTrainerBuilder().build().getUser().toBuilder()
                        .firstName("Elena")
                        .lastName("Rodriguez")
                        .password("newPassword")
                        .isActive(false)
                        .build())
                .specialization(getDefaultTrainingTypeBuilder().id(2L).trainingTypeName("Strength").build())
                .build();

        when(trainingTypeDao.findById(2L)).thenReturn(Optional.of(updateRequest.getSpecialization()));
        when(dao.findById(DEFAULT_TRAINER_ID)).thenReturn(Optional.of(existingTrainer));
        when(dao.update(any(Trainer.class))).thenReturn(expected);

        Trainer actual = service.updateTrainer(updateRequest);

        verify(dao).findById(DEFAULT_TRAINER_ID);
        ArgumentCaptor<Trainer> trainerCaptor = ArgumentCaptor.forClass(Trainer.class);
        verify(dao).update(trainerCaptor.capture());
        Trainer mergedTrainer = trainerCaptor.getValue();
        assertEquals(DEFAULT_TRAINER_ID, mergedTrainer.getId());
        assertEquals(DEFAULT_USERNAME, mergedTrainer.getUser().getUsername());
        assertEquals("Elena", mergedTrainer.getUser().getFirstName());
        assertEquals("Rodriguez", mergedTrainer.getUser().getLastName());
        assertEquals("Strength", mergedTrainer.getSpecialization().getTrainingTypeName());
        assertEquals(expected, actual);
    }

    @Test
    void shouldThrowEntityNotFoundWhenUpdatingUnknownTrainer() {
        Trainer updateRequest = buildTrainerWithId(DEFAULT_TRAINER_ID);

        when(dao.findById(DEFAULT_TRAINER_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.updateTrainer(updateRequest));

        assertEquals("Trainer not found with id: " + DEFAULT_TRAINER_ID, exception.getMessage());
        verify(dao).findById(DEFAULT_TRAINER_ID);
        verify(dao, never()).update(any(Trainer.class));
    }

    @Test
    void shouldThrowNullPointerWhenUpdatingNullTrainer() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.updateTrainer(null));

        assertEquals("Trainer cannot be null", exception.getMessage());
        verifyNoInteractions(dao, generator);
    }

    @Test
    void shouldUpdateTrainerWithoutChangingSpecialization() {
        Trainer existingTrainer = buildTrainerWithId(DEFAULT_TRAINER_ID);
        User updatedUser = existingTrainer.getUser().toBuilder().firstName("NewName").build();
        Trainer updateRequest = existingTrainer.toBuilder().user(updatedUser).build();

        when(dao.findById(DEFAULT_TRAINER_ID)).thenReturn(Optional.of(existingTrainer));
        when(dao.update(any(Trainer.class))).thenReturn(updateRequest);

        service.updateTrainer(updateRequest);

        verify(dao).findById(DEFAULT_TRAINER_ID);
        verify(trainingTypeDao, never()).findById(any());
        verify(dao).update(any(Trainer.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingTrainerWithInvalidSpecialization() {
        Trainer existingTrainer = buildTrainerWithId(DEFAULT_TRAINER_ID);
        Trainer updateRequest = existingTrainer.toBuilder()
                .specialization(TrainingType.builder().id(999L).build())
                .build();

        when(dao.findById(DEFAULT_TRAINER_ID)).thenReturn(Optional.of(existingTrainer));
        when(trainingTypeDao.findById(999L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.updateTrainer(updateRequest));

        assertEquals("TrainingType not found with id: 999", exception.getMessage());
        verify(dao).findById(DEFAULT_TRAINER_ID);
        verify(trainingTypeDao).findById(999L);
        verify(dao, never()).update(any());
    }

    @Test
    void shouldThrowNullPointerWhenUpdatingTrainerWithNullUser() {
        Trainer trainer = Trainer.builder().id(DEFAULT_TRAINER_ID).build();

        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.updateTrainer(trainer));

        assertEquals("Trainer user cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowNullPointerWhenUpdatingTrainerWithNullId() {
        Trainer trainer = buildTrainerWithoutCredentials().toBuilder().id(null).build();

        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> service.updateTrainer(trainer));

        assertEquals("Trainer ID cannot be null", exception.getMessage());
    }

    @Test
    void shouldUpdateTrainerPassword() {
        Trainer trainer = buildTrainerWithId(DEFAULT_TRAINER_ID);
        String newPassword = "newPassword";

        when(dao.findById(DEFAULT_TRAINER_ID)).thenReturn(Optional.of(trainer));

        service.updateTrainerPassword(DEFAULT_TRAINER_ID, newPassword);

        verify(dao).findById(DEFAULT_TRAINER_ID);
        ArgumentCaptor<Trainer> trainerCaptor = ArgumentCaptor.forClass(Trainer.class);
        verify(dao).update(trainerCaptor.capture());
        assertEquals(newPassword, trainerCaptor.getValue().getUser().getPassword());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingPasswordForUnknownTrainer() {
        when(dao.findById(DEFAULT_TRAINER_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.updateTrainerPassword(DEFAULT_TRAINER_ID, "pass"));

        assertEquals("Trainer not found with id: " + DEFAULT_TRAINER_ID, exception.getMessage());
    }

    @Test
    void shouldThrowNullPointerWhenUpdatingPasswordWithNullId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.updateTrainerPassword(null, "pass"));

        assertEquals("Trainer ID cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowNullPointerWhenUpdatingPasswordWithNullPassword() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.updateTrainerPassword(DEFAULT_TRAINER_ID, null));

        assertEquals("Password cannot be null", exception.getMessage());
    }

    @Test
    void shouldActivateTrainer() {
        Trainer trainer = buildTrainerWithId(DEFAULT_TRAINER_ID);
        User inactiveUser = trainer.getUser().toBuilder().isActive(false).build();
        trainer = trainer.toBuilder().user(inactiveUser).build();

        when(dao.findById(DEFAULT_TRAINER_ID)).thenReturn(Optional.of(trainer));

        service.activateTrainer(DEFAULT_TRAINER_ID);

        verify(dao).findById(DEFAULT_TRAINER_ID);
        ArgumentCaptor<Trainer> trainerCaptor = ArgumentCaptor.forClass(Trainer.class);
        verify(dao).update(trainerCaptor.capture());
        assertEquals(true, trainerCaptor.getValue().getUser().getIsActive());
    }

    @Test
    void shouldThrowExceptionWhenActivatingAlreadyActiveTrainer() {
        Trainer trainer = buildTrainerWithId(DEFAULT_TRAINER_ID);
        User activeUser = trainer.getUser().toBuilder().isActive(true).build();
        trainer = trainer.toBuilder().user(activeUser).build();

        when(dao.findById(DEFAULT_TRAINER_ID)).thenReturn(Optional.of(trainer));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> service.activateTrainer(DEFAULT_TRAINER_ID));

        assertEquals("Trainer is already active", exception.getMessage());
        verify(dao, never()).update(any());
    }

    @Test
    void shouldThrowNullPointerWhenActivatingWithNullId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.activateTrainer(null));

        assertEquals("Trainer ID cannot be null", exception.getMessage());
        verifyNoInteractions(dao);
    }

    @Test
    void shouldThrowEntityNotFoundWhenActivatingNonExistingTrainer() {
        when(dao.findById(DEFAULT_TRAINER_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.activateTrainer(DEFAULT_TRAINER_ID));

        assertEquals("Trainer not found with id: " + DEFAULT_TRAINER_ID, exception.getMessage());
        verify(dao).findById(DEFAULT_TRAINER_ID);
    }

    @Test
    void shouldDeactivateTrainer() {
        Trainer trainer = buildTrainerWithId(DEFAULT_TRAINER_ID);
        User activeUser = trainer.getUser().toBuilder().isActive(true).build();
        trainer = trainer.toBuilder().user(activeUser).build();

        when(dao.findById(DEFAULT_TRAINER_ID)).thenReturn(Optional.of(trainer));

        service.deactivateTrainer(DEFAULT_TRAINER_ID);

        verify(dao).findById(DEFAULT_TRAINER_ID);
        ArgumentCaptor<Trainer> trainerCaptor = ArgumentCaptor.forClass(Trainer.class);
        verify(dao).update(trainerCaptor.capture());
        assertEquals(false, trainerCaptor.getValue().getUser().getIsActive());
    }

    @Test
    void shouldThrowExceptionWhenDeactivatingAlreadyDeactivatedTrainer() {
        Trainer trainer = buildTrainerWithId(DEFAULT_TRAINER_ID);
        User inactiveUser = trainer.getUser().toBuilder().isActive(false).build();
        trainer = trainer.toBuilder().user(inactiveUser).build();

        when(dao.findById(DEFAULT_TRAINER_ID)).thenReturn(Optional.of(trainer));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> service.deactivateTrainer(DEFAULT_TRAINER_ID));

        assertEquals("Trainer is already deactivated", exception.getMessage());
        verify(dao, never()).update(any());
    }

    @Test
    void shouldThrowNullPointerWhenDeactivatingWithNullId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.deactivateTrainer(null));

        assertEquals("Trainer ID cannot be null", exception.getMessage());
        verifyNoInteractions(dao);
    }

    @Test
    void shouldThrowEntityNotFoundWhenDeactivatingNonExistingTrainer() {
        when(dao.findById(DEFAULT_TRAINER_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.deactivateTrainer(DEFAULT_TRAINER_ID));

        assertEquals("Trainer not found with id: " + DEFAULT_TRAINER_ID, exception.getMessage());
        verify(dao).findById(DEFAULT_TRAINER_ID);
    }

}
