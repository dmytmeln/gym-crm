package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.service.ProfileCredentialService;
import com.gym.crm.service.TraineeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_FIRST_NAME;
import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_LAST_NAME;
import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_PASSWORD;
import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_TRAINEE_ID;
import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_USERNAME;
import static com.gym.crm.factory.TraineeTestFactory.SECONDARY_TRAINEE_ID;
import static com.gym.crm.factory.TraineeTestFactory.defaultTrainee;
import static com.gym.crm.factory.TraineeTestFactory.traineeWithId;
import static com.gym.crm.factory.TraineeTestFactory.traineeWithoutCredentials;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TraineeServiceImplTest {

    @Mock
    private TraineeDao dao;

    @Mock
    private ProfileCredentialService credentialService;

    private TraineeService service;

    @BeforeEach
    public void setUp() {
        TraineeServiceImpl implementation = new TraineeServiceImpl();
        implementation.setTraineeDao(dao);
        implementation.setCredentialService(credentialService);
        service = implementation;
    }

    @Test
    public void shouldCreateTraineeWithGeneratedCredentials() {
        Trainee traineeWithoutCredentials = traineeWithoutCredentials();
        Trainee createdTrainee = traineeWithId(DEFAULT_TRAINEE_ID);
        when(credentialService.generateUsername(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME)).thenReturn(DEFAULT_USERNAME);
        when(credentialService.generatePassword()).thenReturn(DEFAULT_PASSWORD);
        when(dao.create(any(Trainee.class))).thenReturn(createdTrainee);

        Trainee result = service.createTrainee(traineeWithoutCredentials);

        assertCredentialsWereGeneratedForTrainee();
        assertEquals(createdTrainee, result);
    }

    @Test
    public void shouldThrowNullPointerWhenCreatingNullTrainee() {
        assertThrows(NullPointerException.class, () -> service.createTrainee(null));

        verifyNoInteractions(dao, credentialService);
    }

    @Test
    public void shouldMergeUpdatedFieldsAndPreserveExistingUsernameWhenUpdatingTrainee() {
        Trainee existingTrainee = traineeWithId(DEFAULT_TRAINEE_ID);
        Trainee updateRequest = defaultTrainee()
                .userId(DEFAULT_TRAINEE_ID)
                .firstName("Jane")
                .lastName("Smith")
                .address("456 Oak Ave")
                .build();
        Trainee updatedTrainee = defaultTrainee()
                .userId(DEFAULT_TRAINEE_ID)
                .firstName("Jane")
                .lastName("Smith")
                .address("456 Oak Ave")
                .build();
        when(dao.findById(DEFAULT_TRAINEE_ID)).thenReturn(Optional.of(existingTrainee));
        when(dao.update(any(Trainee.class))).thenReturn(updatedTrainee);

        Trainee result = service.updateTrainee(updateRequest);

        verify(dao).findById(DEFAULT_TRAINEE_ID);
        assertMergedTraineeHasCorrectFields("Jane", "Smith", "456 Oak Ave");
        assertEquals(updatedTrainee, result);
    }

    @Test
    public void shouldThrowEntityNotFoundWhenUpdatingUnknownTrainee() {
        Trainee updateRequest = traineeWithId(DEFAULT_TRAINEE_ID);
        when(dao.findById(DEFAULT_TRAINEE_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.updateTrainee(updateRequest));

        verify(dao).findById(DEFAULT_TRAINEE_ID);
        verify(dao, never()).update(any(Trainee.class));
    }

    @Test
    public void shouldThrowNullPointerWhenUpdatingNullTrainee() {
        assertThrows(NullPointerException.class, () -> service.updateTrainee(null));

        verifyNoInteractions(dao, credentialService);
    }

    @Test
    public void shouldDeleteTrainee() {
        when(dao.delete(DEFAULT_TRAINEE_ID)).thenReturn(true);

        boolean result = service.deleteTrainee(DEFAULT_TRAINEE_ID);

        assertTrue(result);
        verify(dao).delete(DEFAULT_TRAINEE_ID);
    }

    @Test
    public void shouldThrowNullPointerWhenDeletingNullTraineeId() {
        assertThrows(NullPointerException.class, () -> service.deleteTrainee(null));

        verifyNoInteractions(dao, credentialService);
    }

    @Test
    public void shouldReturnTraineeWhenGettingExistingTrainee() {
        Trainee existingTrainee = traineeWithId(DEFAULT_TRAINEE_ID);
        when(dao.findById(DEFAULT_TRAINEE_ID)).thenReturn(Optional.of(existingTrainee));

        Trainee result = service.getTrainee(DEFAULT_TRAINEE_ID);

        assertEquals(existingTrainee, result);
        verify(dao).findById(DEFAULT_TRAINEE_ID);
    }

    @Test
    public void shouldThrowEntityNotFoundWhenGettingUnknownTrainee() {
        when(dao.findById(DEFAULT_TRAINEE_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getTrainee(DEFAULT_TRAINEE_ID));

        verify(dao).findById(DEFAULT_TRAINEE_ID);
    }

    @Test
    public void shouldThrowNullPointerWhenGettingNullTraineeId() {
        assertThrows(NullPointerException.class, () -> service.getTrainee(null));

        verifyNoInteractions(dao, credentialService);
    }

    @Test
    public void shouldReturnAllTrainees() {
        Trainee trainee1 = traineeWithId(DEFAULT_TRAINEE_ID);
        Trainee trainee2 = traineeWithId(SECONDARY_TRAINEE_ID);
        List<Trainee> trainees = List.of(trainee1, trainee2);
        when(dao.findAll()).thenReturn(trainees);

        List<Trainee> result = service.getAllTrainees();

        assertNotNull(result);
        assertEquals(trainees, result);
    }

    @Test
    public void shouldReturnEmptyTrainees() {
        when(dao.findAll()).thenReturn(List.of());

        List<Trainee> result = service.getAllTrainees();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    private void assertCredentialsWereGeneratedForTrainee() {
        verify(credentialService).generateUsername(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME);
        verify(credentialService).generatePassword();
        ArgumentCaptor<Trainee> traineeCaptor = ArgumentCaptor.forClass(Trainee.class);
        verify(dao).create(traineeCaptor.capture());
        Trainee traineeToCreate = traineeCaptor.getValue();
        assertEquals(DEFAULT_USERNAME, traineeToCreate.getUsername());
        assertEquals(DEFAULT_PASSWORD, traineeToCreate.getPassword());
        assertEquals(DEFAULT_FIRST_NAME, traineeToCreate.getFirstName());
        assertEquals(DEFAULT_LAST_NAME, traineeToCreate.getLastName());
    }

    private void assertMergedTraineeHasCorrectFields(String firstName, String lastName, String address) {
        ArgumentCaptor<Trainee> traineeCaptor = ArgumentCaptor.forClass(Trainee.class);
        verify(dao).update(traineeCaptor.capture());
        Trainee traineeToUpdate = traineeCaptor.getValue();
        assertEquals(DEFAULT_TRAINEE_ID, traineeToUpdate.getUserId());
        assertEquals(DEFAULT_USERNAME, traineeToUpdate.getUsername());
        assertEquals(firstName, traineeToUpdate.getFirstName());
        assertEquals(lastName, traineeToUpdate.getLastName());
        assertEquals(address, traineeToUpdate.getAddress());
    }

}
