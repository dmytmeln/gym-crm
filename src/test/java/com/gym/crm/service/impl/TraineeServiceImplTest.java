package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeEntityDao;
import com.gym.crm.dao.TrainerEntityDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.helper.ProfileCredentialGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_FIRST_NAME;
import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_LAST_NAME;
import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_PASSWORD;
import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_TRAINEE_ID;
import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_USERNAME;
import static com.gym.crm.factory.TraineeTestFactory.NON_EXISTENT_TRAINEE_ID;
import static com.gym.crm.factory.TraineeTestFactory.SECONDARY_TRAINEE_ID;
import static com.gym.crm.factory.TraineeTestFactory.buildTrainee;
import static com.gym.crm.factory.TraineeTestFactory.buildTraineeWithId;
import static com.gym.crm.factory.TraineeTestFactory.buildTraineeWithUsername;
import static com.gym.crm.factory.TraineeTestFactory.buildTraineeWithoutCredentials;
import static com.gym.crm.factory.TraineeTestFactory.buildTraineeWithoutUser;
import static com.gym.crm.factory.TraineeTestFactory.getDefaultTraineeBuilder;
import static com.gym.crm.factory.TraineeTestFactory.getDefaultUserBuilder;
import static com.gym.crm.factory.TrainerTestFactory.buildTrainerWithId;
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
class TraineeServiceImplTest {

    @Mock
    private TraineeEntityDao dao;

    @Mock
    private TrainerEntityDao trainerDao;

    @Mock
    private ProfileCredentialGenerator generator;

    private TraineeService service;

    @BeforeEach
    void setUp() {
        TraineeServiceImpl implementation = new TraineeServiceImpl();
        implementation.setTraineeDao(dao);
        implementation.setTrainerDao(trainerDao);
        implementation.setCredentialGenerator(generator);
        service = implementation;
    }

    @Test
    void shouldCreateTraineeWithGeneratedCredentials() {
        Trainee traineeWithoutCredentials = buildTraineeWithoutCredentials();
        Trainee expected = buildTraineeWithId();

        when(generator.generateUsername(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME)).thenReturn(DEFAULT_USERNAME);
        when(generator.generatePassword()).thenReturn(DEFAULT_PASSWORD);
        when(dao.save(any(Trainee.class))).thenReturn(expected);

        Trainee actual = service.createTrainee(traineeWithoutCredentials);

        verify(generator).generateUsername(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME);
        verify(generator).generatePassword();
        ArgumentCaptor<Trainee> traineeCaptor = ArgumentCaptor.forClass(Trainee.class);
        verify(dao).save(traineeCaptor.capture());
        Trainee traineeWithCredentials = traineeCaptor.getValue();
        assertEquals(DEFAULT_USERNAME, traineeWithCredentials.getUser().getUsername());
        assertEquals(DEFAULT_PASSWORD, traineeWithCredentials.getUser().getPassword());
        assertEquals(DEFAULT_FIRST_NAME, traineeWithCredentials.getUser().getFirstName());
        assertEquals(DEFAULT_LAST_NAME, traineeWithCredentials.getUser().getLastName());
        assertEquals(expected, actual);
    }

    @Test
    void shouldThrowNullPointerWhenCreatingNullTrainee() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.createTrainee(null));

        assertEquals("Trainee cannot be null", exception.getMessage());
        verifyNoInteractions(dao, generator);
    }

    @Test
    void shouldThrowNullPointerWhenCreatingTraineeWithNullUser() {
        Trainee traineeWithoutUser = buildTraineeWithoutUser();

        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.createTrainee(traineeWithoutUser));

        assertEquals("User cannot be null", exception.getMessage());
        verifyNoInteractions(dao, generator);
    }

    @Test
    void shouldReturnTraineeWhenGettingExistingTrainee() {
        Trainee expected = buildTraineeWithId();

        when(dao.findById(DEFAULT_TRAINEE_ID)).thenReturn(Optional.of(expected));

        Trainee actual = service.getTrainee(DEFAULT_TRAINEE_ID);

        assertEquals(expected, actual);
        verify(dao).findById(DEFAULT_TRAINEE_ID);
    }

    @Test
    void shouldThrowEntityNotFoundWhenGettingUnknownTrainee() {
        when(dao.findById(DEFAULT_TRAINEE_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.getTrainee(DEFAULT_TRAINEE_ID));

        assertEquals("Trainee not found with id: " + DEFAULT_TRAINEE_ID, exception.getMessage());
        verify(dao).findById(DEFAULT_TRAINEE_ID);
    }

    @Test
    void shouldThrowNullPointerWhenGettingNullTraineeId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.getTrainee(null));

        assertEquals("Trainee ID cannot be null", exception.getMessage());
        verifyNoInteractions(dao, generator);
    }

    @Test
    void shouldReturnTraineeWhenGettingByExistingUsername() {
        Trainee expected = buildTraineeWithUsername(DEFAULT_USERNAME);

        when(dao.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(expected));

        Trainee actual = service.getTraineeByUsername(DEFAULT_USERNAME);

        assertEquals(expected, actual);
        verify(dao).findByUsername(DEFAULT_USERNAME);
    }

    @Test
    void shouldThrowEntityNotFoundWhenGettingByNonExistentUsername() {
        when(dao.findByUsername("unknown")).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.getTraineeByUsername("unknown"));

        assertEquals("Trainee not found with username: unknown", exception.getMessage());
        verify(dao).findByUsername("unknown");
    }

    @Test
    void shouldThrowNullPointerWhenGettingByNullUsername() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.getTraineeByUsername(null));

        assertEquals("Trainee username cannot be null", exception.getMessage());
        verifyNoInteractions(dao);
    }

    @Test
    void shouldReturnAllTrainees() {
        Trainee trainee1 = buildTraineeWithId(DEFAULT_TRAINEE_ID);
        Trainee trainee2 = buildTraineeWithId(SECONDARY_TRAINEE_ID);
        List<Trainee> expected = List.of(trainee1, trainee2);

        when(dao.findAll()).thenReturn(expected);

        List<Trainee> actual = service.getAllTrainees();

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void shouldReturnEmptyTrainees() {
        List<Trainee> expected = List.of();

        when(dao.findAll()).thenReturn(expected);

        List<Trainee> actual = service.getAllTrainees();

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void shouldReturnTrueWhenUsernameAndPasswordMatch() {
        Trainee trainee = buildTraineeWithUsername(DEFAULT_USERNAME);

        when(dao.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(trainee));

        boolean result = service.doesUsernameAndPasswordMatch(DEFAULT_USERNAME, DEFAULT_PASSWORD);

        assertTrue(result);
        verify(dao).findByUsername(DEFAULT_USERNAME);
    }

    @Test
    void shouldReturnFalseWhenPasswordDoesNotMatch() {
        Trainee trainee = buildTraineeWithUsername(DEFAULT_USERNAME);

        when(dao.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(trainee));

        boolean result = service.doesUsernameAndPasswordMatch(DEFAULT_USERNAME, "wrongPassword");

        assertFalse(result);
        verify(dao).findByUsername(DEFAULT_USERNAME);
    }

    @Test
    void shouldThrowNullPointerWhenUsernameIsNull() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> service.doesUsernameAndPasswordMatch(null, DEFAULT_PASSWORD));

        assertEquals("Trainee username cannot be null", exception.getMessage());
        verifyNoInteractions(dao);
    }

    @Test
    void shouldThrowNullPointerWhenPasswordIsNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.doesUsernameAndPasswordMatch(DEFAULT_USERNAME, null));

        assertEquals("Password cannot be null", exception.getMessage());
        verifyNoInteractions(dao);
    }

    @Test
    void shouldThrowEntityNotFoundWhenCheckingPasswordForUnknownUsername() {
        String nonExistingUsername = "unknown";
        when(dao.findByUsername(nonExistingUsername)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.doesUsernameAndPasswordMatch(nonExistingUsername, "password"));

        verify(dao).findByUsername(nonExistingUsername);
    }

    @Test
    void shouldMergeUpdatedFieldsAndPreserveExistingUsernameWhenUpdatingTrainee() {
        Trainee existingTrainee = buildTraineeWithId();
        Trainee updateRequest = getDefaultTraineeBuilder()
                .id(DEFAULT_TRAINEE_ID)
                .user(getDefaultUserBuilder()
                        .firstName("Sophia")
                        .lastName("Wilson")
                        .password("newPassword")
                        .isActive(false)
                        .build())
                .address("456 Oak Ave")
                .build();

        when(dao.findById(DEFAULT_TRAINEE_ID)).thenReturn(Optional.of(existingTrainee));
        when(dao.update(any(Trainee.class))).thenReturn(updateRequest);

        Trainee actual = service.updateTrainee(updateRequest);

        verify(dao).findById(DEFAULT_TRAINEE_ID);
        ArgumentCaptor<Trainee> traineeCaptor = ArgumentCaptor.forClass(Trainee.class);
        verify(dao).update(traineeCaptor.capture());
        Trainee mergedTrainee = traineeCaptor.getValue();
        assertEquals(DEFAULT_TRAINEE_ID, mergedTrainee.getId());
        assertEquals(DEFAULT_USERNAME, mergedTrainee.getUser().getUsername());
        assertEquals(updateRequest.getUser().getFirstName(), mergedTrainee.getUser().getFirstName());
        assertEquals(updateRequest.getUser().getLastName(), mergedTrainee.getUser().getLastName());
        assertEquals(updateRequest.getAddress(), mergedTrainee.getAddress());
        assertEquals(updateRequest, actual);
    }

    @Test
    void shouldThrowEntityNotFoundWhenUpdatingUnknownTrainee() {
        Trainee updateRequest = buildTraineeWithId();

        when(dao.findById(DEFAULT_TRAINEE_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.updateTrainee(updateRequest));

        assertEquals("Trainee not found with id: " + DEFAULT_TRAINEE_ID, exception.getMessage());
        verify(dao).findById(DEFAULT_TRAINEE_ID);
        verify(dao, never()).update(any(Trainee.class));
    }

    @Test
    void shouldThrowNullPointerWhenUpdatingNullTrainee() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.updateTrainee(null));

        assertEquals("Trainee cannot be null", exception.getMessage());
        verifyNoInteractions(dao, generator);
    }

    @Test
    void shouldThrowNullPointerWhenUpdatingTraineeWithoutId() {
        Trainee traineeWithoutId = buildTrainee();

        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.updateTrainee(traineeWithoutId));

        assertEquals("Trainee ID cannot be null", exception.getMessage());
        verifyNoInteractions(dao, generator);
    }

    @Test
    void shouldThrowNullPointerWhenUpdatingTraineeWithoutUser() {
        Trainee traineeWithoutUser = getDefaultTraineeBuilder()
                .id(DEFAULT_TRAINEE_ID)
                .user(null)
                .build();

        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.updateTrainee(traineeWithoutUser));

        assertEquals("User cannot be null", exception.getMessage());
        verifyNoInteractions(dao, generator);
    }

    @Test
    void shouldUpdateTraineeTrainersSuccessfully() {
        Trainee trainee = buildTraineeWithId();
        Trainer trainer1 = buildTrainerWithId(1L);
        Trainer trainer2 = buildTrainerWithId(2L);
        List<Long> trainerIds = List.of(1L, 2L);

        when(dao.findById(DEFAULT_TRAINEE_ID)).thenReturn(Optional.of(trainee));
        when(trainerDao.findAllByIds(trainerIds)).thenReturn(List.of(trainer1, trainer2));
        when(dao.update(any(Trainee.class))).thenReturn(trainee);

        Trainee result = service.updateTraineeTrainers(DEFAULT_TRAINEE_ID, trainerIds);

        assertNotNull(result);
        verify(dao).findById(DEFAULT_TRAINEE_ID);
        verify(trainerDao).findAllByIds(trainerIds);
        verify(dao).update(any(Trainee.class));
    }

    @Test
    void shouldClearAllTrainersWhenUpdatingWithEmptyList() {
        Trainee trainee = buildTraineeWithId();
        trainee.addTrainer(buildTrainerWithId(99L));

        when(dao.findById(DEFAULT_TRAINEE_ID)).thenReturn(Optional.of(trainee));
        when(trainerDao.findAllByIds(Collections.emptyList())).thenReturn(Collections.emptyList());
        when(dao.update(any(Trainee.class))).thenReturn(trainee);

        service.updateTraineeTrainers(DEFAULT_TRAINEE_ID, Collections.emptyList());

        assertTrue(trainee.getTrainers().isEmpty());
        verify(dao).update(any(Trainee.class));
    }

    @Test
    void shouldThrowEntityNotFoundWhenUpdatingTrainersForUnknownTrainee() {
        List<Long> trainerIds = List.of(1L);

        when(dao.findById(NON_EXISTENT_TRAINEE_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.updateTraineeTrainers(NON_EXISTENT_TRAINEE_ID, trainerIds));

        assertEquals("Trainee not found with id: " + NON_EXISTENT_TRAINEE_ID, exception.getMessage());
        verify(dao).findById(NON_EXISTENT_TRAINEE_ID);
        verify(trainerDao, never()).findAllByIds(any());
        verify(dao, never()).update(any(Trainee.class));
    }

    @Test
    void shouldThrowNullPointerWhenUpdatingTrainersWithNullTraineeId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.updateTraineeTrainers(null, List.of(1L)));

        assertEquals("Trainee ID cannot be null", exception.getMessage());
        verifyNoInteractions(dao, trainerDao);
    }

    @Test
    void shouldThrowNullPointerWhenUpdatingTrainersWithNullTrainerIds() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.updateTraineeTrainers(DEFAULT_TRAINEE_ID, null));

        assertEquals("Trainer IDs cannot be null", exception.getMessage());
        verifyNoInteractions(dao, trainerDao);
    }

    @Test
    void shouldUpdateTraineePasswordSuccessfully() {
        Trainee trainee = buildTraineeWithId();
        String newPassword = "newSecurePassword123";

        when(dao.findById(DEFAULT_TRAINEE_ID)).thenReturn(Optional.of(trainee));

        service.updateTraineePassword(DEFAULT_TRAINEE_ID, newPassword);

        ArgumentCaptor<Trainee> captor = ArgumentCaptor.forClass(Trainee.class);
        verify(dao).update(captor.capture());
        assertEquals(newPassword, captor.getValue().getUser().getPassword());
    }

    @Test
    void shouldThrowEntityNotFoundWhenUpdatingPasswordForUnknownTrainee() {
        when(dao.findById(NON_EXISTENT_TRAINEE_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> service.updateTraineePassword(NON_EXISTENT_TRAINEE_ID, "newPassword"));

        assertEquals("Trainee not found with id: " + NON_EXISTENT_TRAINEE_ID, exception.getMessage());
        verify(dao).findById(NON_EXISTENT_TRAINEE_ID);
        verify(dao, never()).update(any(Trainee.class));
    }

    @Test
    void shouldThrowNullPointerWhenUpdatingPasswordWithNullTraineeId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.updateTraineePassword(null, "password"));

        assertEquals("Trainee ID cannot be null", exception.getMessage());
        verifyNoInteractions(dao);
    }

    @Test
    void shouldThrowNullPointerWhenUpdatingPasswordWithNullPassword() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.updateTraineePassword(DEFAULT_TRAINEE_ID, null));

        assertEquals("Password cannot be null", exception.getMessage());
        verifyNoInteractions(dao);
    }

    @Test
    void shouldActivateTraineeSuccessfully() {
        Trainee inactiveTrainee = getDefaultTraineeBuilder()
                .id(DEFAULT_TRAINEE_ID)
                .user(getDefaultUserBuilder().isActive(false).build())
                .build();

        when(dao.findById(DEFAULT_TRAINEE_ID)).thenReturn(Optional.of(inactiveTrainee));

        service.activateTrainee(DEFAULT_TRAINEE_ID);

        ArgumentCaptor<Trainee> captor = ArgumentCaptor.forClass(Trainee.class);
        verify(dao).update(captor.capture());
        assertTrue(captor.getValue().getUser().getIsActive());
    }

    @Test
    void shouldThrowEntityNotFoundWhenActivatingUnknownTrainee() {
        when(dao.findById(NON_EXISTENT_TRAINEE_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.activateTrainee(NON_EXISTENT_TRAINEE_ID));

        assertEquals("Trainee not found with id: " + NON_EXISTENT_TRAINEE_ID, exception.getMessage());
        verify(dao, never()).update(any(Trainee.class));
    }

    @Test
    void shouldThrowIllegalStateWhenActivatingAlreadyActiveTrainee() {
        Trainee activeTrainee = buildTraineeWithId();

        when(dao.findById(DEFAULT_TRAINEE_ID)).thenReturn(Optional.of(activeTrainee));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> service.activateTrainee(DEFAULT_TRAINEE_ID));

        assertEquals("Trainee is already active", exception.getMessage());
        verify(dao, never()).update(any(Trainee.class));
    }

    @Test
    void shouldThrowNullPointerWhenActivatingWithNullTraineeId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.activateTrainee(null));

        assertEquals("Trainee ID cannot be null", exception.getMessage());
        verifyNoInteractions(dao);
    }

    @Test
    void shouldDeactivateTraineeSuccessfully() {
        Trainee activeTrainee = buildTraineeWithId();

        when(dao.findById(DEFAULT_TRAINEE_ID)).thenReturn(Optional.of(activeTrainee));

        service.deactivateTrainee(DEFAULT_TRAINEE_ID);

        ArgumentCaptor<Trainee> captor = ArgumentCaptor.forClass(Trainee.class);
        verify(dao).update(captor.capture());
        assertFalse(captor.getValue().getUser().getIsActive());
    }

    @Test
    void shouldThrowEntityNotFoundWhenDeactivatingUnknownTrainee() {
        when(dao.findById(NON_EXISTENT_TRAINEE_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.deactivateTrainee(NON_EXISTENT_TRAINEE_ID));

        assertEquals("Trainee not found with id: " + NON_EXISTENT_TRAINEE_ID, exception.getMessage());
        verify(dao, never()).update(any(Trainee.class));
    }

    @Test
    void shouldThrowIllegalStateWhenDeactivatingAlreadyInactiveTrainee() {
        Trainee inactiveTrainee = getDefaultTraineeBuilder()
                .id(DEFAULT_TRAINEE_ID)
                .user(getDefaultUserBuilder().isActive(false).build())
                .build();

        when(dao.findById(DEFAULT_TRAINEE_ID)).thenReturn(Optional.of(inactiveTrainee));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> service.deactivateTrainee(DEFAULT_TRAINEE_ID));

        assertEquals("Trainee is already deactivated", exception.getMessage());
        verify(dao, never()).update(any(Trainee.class));
    }

    @Test
    void shouldThrowNullPointerWhenDeactivatingWithNullTraineeId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.deactivateTrainee(null));

        assertEquals("Trainee ID cannot be null", exception.getMessage());
        verifyNoInteractions(dao);
    }

    @Test
    void shouldDeleteTrainee() {
        when(dao.deleteById(DEFAULT_TRAINEE_ID)).thenReturn(true);

        boolean actual = service.deleteTrainee(DEFAULT_TRAINEE_ID);

        assertTrue(actual);
        verify(dao).deleteById(DEFAULT_TRAINEE_ID);
    }

    @Test
    void shouldReturnFalseWhenDeletingNonExistentTrainee() {
        when(dao.deleteById(NON_EXISTENT_TRAINEE_ID)).thenReturn(false);

        boolean actual = service.deleteTrainee(NON_EXISTENT_TRAINEE_ID);

        assertFalse(actual);
        verify(dao).deleteById(NON_EXISTENT_TRAINEE_ID);
    }

    @Test
    void shouldThrowNullPointerWhenDeletingNullTraineeId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.deleteTrainee(null));

        assertEquals("Trainee ID cannot be null", exception.getMessage());
        verifyNoInteractions(dao, generator);
    }

    @Test
    void shouldDeleteTraineeByUsernameSuccessfully() {
        when(dao.deleteByUsername(DEFAULT_USERNAME)).thenReturn(true);

        boolean result = service.deleteTraineeByUsername(DEFAULT_USERNAME);

        assertTrue(result);
        verify(dao).deleteByUsername(DEFAULT_USERNAME);
    }

    @Test
    void shouldReturnFalseWhenDeletingByNonExistentUsername() {
        String nonExistingUsername = "unknown";
        when(dao.deleteByUsername(nonExistingUsername)).thenReturn(false);

        boolean result = service.deleteTraineeByUsername(nonExistingUsername);

        assertFalse(result);
        verify(dao).deleteByUsername(nonExistingUsername);
    }

    @Test
    void shouldThrowNullPointerWhenDeletingByNullUsername() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.deleteTraineeByUsername(null));

        assertEquals("Trainee username cannot be null", exception.getMessage());
        verifyNoInteractions(dao);
    }

}
