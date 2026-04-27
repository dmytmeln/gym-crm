package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.model.Trainee;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.service.helper.ProfileCredentialGenerator;
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
import static com.gym.crm.factory.TraineeTestFactory.buildTraineeWithId;
import static com.gym.crm.factory.TraineeTestFactory.buildTraineeWithoutCredentials;
import static com.gym.crm.factory.TraineeTestFactory.getDefaultTraineeBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    @Mock
    private TraineeDao dao;

    @Mock
    private ProfileCredentialGenerator generator;

    private TraineeService service;

    @BeforeEach
    void setUp() {
        TraineeServiceImpl implementation = new TraineeServiceImpl();
        implementation.setTraineeDao(dao);
        implementation.setCredentialGenerator(generator);
        service = implementation;
    }

    @Test
    void shouldCreateTraineeWithGeneratedCredentials() {
        Trainee traineeWithoutCredentials = buildTraineeWithoutCredentials();
        Trainee expected = buildTraineeWithId(DEFAULT_TRAINEE_ID);

        when(generator.generateUsername(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME)).thenReturn(DEFAULT_USERNAME);
        when(generator.generatePassword()).thenReturn(DEFAULT_PASSWORD);
        when(dao.create(any(Trainee.class))).thenReturn(expected);

        Trainee actual = service.createTrainee(traineeWithoutCredentials);

        verify(generator).generateUsername(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME);
        verify(generator).generatePassword();
        ArgumentCaptor<Trainee> traineeCaptor = ArgumentCaptor.forClass(Trainee.class);
        verify(dao).create(traineeCaptor.capture());
        Trainee traineeWithCredentials = traineeCaptor.getValue();
        assertEquals(DEFAULT_USERNAME, traineeWithCredentials.getUsername());
        assertEquals(DEFAULT_PASSWORD, traineeWithCredentials.getPassword());
        assertEquals(DEFAULT_FIRST_NAME, traineeWithCredentials.getFirstName());
        assertEquals(DEFAULT_LAST_NAME, traineeWithCredentials.getLastName());
        assertEquals(expected, actual);
    }

    @Test
    void shouldThrowNullPointerWhenCreatingNullTrainee() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.createTrainee(null));

        assertEquals("Trainee cannot be null", exception.getMessage());
        verifyNoInteractions(dao, generator);
    }

    @Test
    void shouldMergeUpdatedFieldsAndPreserveExistingUsernameWhenUpdatingTrainee() {
        Trainee existingTrainee = buildTraineeWithId(DEFAULT_TRAINEE_ID);
        Trainee updateRequest = getDefaultTraineeBuilder()
                .userId(DEFAULT_TRAINEE_ID)
                .firstName("Sophia")
                .lastName("Wilson")
                .address("456 Oak Ave")
                .build();
        Trainee expected = getDefaultTraineeBuilder()
                .userId(DEFAULT_TRAINEE_ID)
                .firstName("Sophia")
                .lastName("Wilson")
                .address("456 Oak Ave")
                .build();

        when(dao.findById(DEFAULT_TRAINEE_ID)).thenReturn(Optional.of(existingTrainee));
        when(dao.update(any(Trainee.class))).thenReturn(expected);

        Trainee actual = service.updateTrainee(updateRequest);

        verify(dao).findById(DEFAULT_TRAINEE_ID);
        ArgumentCaptor<Trainee> traineeCaptor = ArgumentCaptor.forClass(Trainee.class);
        verify(dao).update(traineeCaptor.capture());
        Trainee mergedTrainee = traineeCaptor.getValue();
        assertEquals(DEFAULT_TRAINEE_ID, mergedTrainee.getUserId());
        assertEquals(DEFAULT_USERNAME, mergedTrainee.getUsername());
        assertEquals("Sophia", mergedTrainee.getFirstName());
        assertEquals("Wilson", mergedTrainee.getLastName());
        assertEquals("456 Oak Ave", mergedTrainee.getAddress());
        assertEquals(expected, actual);
    }

    @Test
    void shouldThrowEntityNotFoundWhenUpdatingUnknownTrainee() {
        Trainee updateRequest = buildTraineeWithId(DEFAULT_TRAINEE_ID);

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
    void shouldDeleteTrainee() {
        boolean expected = true;

        when(dao.delete(DEFAULT_TRAINEE_ID)).thenReturn(expected);

        boolean actual = service.deleteTrainee(DEFAULT_TRAINEE_ID);

        assertEquals(expected, actual);
        verify(dao).delete(DEFAULT_TRAINEE_ID);
    }

    @Test
    void shouldThrowNullPointerWhenDeletingNullTraineeId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.deleteTrainee(null));

        assertEquals("Trainee ID cannot be null", exception.getMessage());
        verifyNoInteractions(dao, generator);
    }

    @Test
    void shouldReturnTraineeWhenGettingExistingTrainee() {
        Trainee expected = buildTraineeWithId(DEFAULT_TRAINEE_ID);

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

}
