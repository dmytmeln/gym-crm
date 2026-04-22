package com.gym.crm.storage.impl;

import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import com.gym.crm.storage.Namespace;
import com.gym.crm.storage.NamespaceStorage;
import com.gym.crm.storage.Storage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_TRAINEE_ID;
import static com.gym.crm.factory.TraineeTestFactory.NON_EXISTENT_TRAINEE_ID;
import static com.gym.crm.factory.TraineeTestFactory.buildTrainee;
import static com.gym.crm.factory.TraineeTestFactory.buildTraineeWithId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InMemoryStorageTest {

    @Mock
    private NamespaceStorage<Trainee> traineeStorage;

    @Mock
    private NamespaceStorage<Trainer> trainerStorage;

    @Mock
    private NamespaceStorage<Training> trainingStorage;

    private Storage storage;

    @BeforeEach
    void setUp() {
        when(traineeStorage.getNamespace()).thenReturn(Namespace.TRAINEE);
        when(trainerStorage.getNamespace()).thenReturn(Namespace.TRAINER);
        when(trainingStorage.getNamespace()).thenReturn(Namespace.TRAINING);

        InMemoryStorage implementation = new InMemoryStorage();
        implementation.setNamespaceStorages(List.of(traineeStorage, trainerStorage, trainingStorage));
        storage = implementation;
    }

    @Test
    void shouldSaveEntityToCorrectNamespaceStorage() {
        Trainee trainee = buildTraineeWithId(DEFAULT_TRAINEE_ID);

        when(traineeStorage.save(any(Trainee.class))).thenReturn(trainee);

        Trainee result = storage.save(Namespace.TRAINEE, trainee);

        assertEquals(trainee, result);
        verify(traineeStorage).save(trainee);
    }

    @Test
    void shouldUpdateEntityInCorrectNamespaceStorage() {
        Trainee trainee = buildTraineeWithId(DEFAULT_TRAINEE_ID);

        storage.update(Namespace.TRAINEE, DEFAULT_TRAINEE_ID, trainee);

        verify(traineeStorage).update(DEFAULT_TRAINEE_ID, trainee);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenUpdatingWithNullId() {
        Trainee trainee = buildTrainee();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> storage.update(Namespace.TRAINEE, null, trainee));

        assertEquals("ID cannot be null", exception.getMessage());
    }

    @Test
    void shouldFindByIdFromCorrectNamespaceStorage() {
        Trainee trainee = buildTraineeWithId(DEFAULT_TRAINEE_ID);

        when(traineeStorage.findById(DEFAULT_TRAINEE_ID)).thenReturn(Optional.of(trainee));

        Optional<Trainee> result = storage.findById(Namespace.TRAINEE, DEFAULT_TRAINEE_ID);

        assertTrue(result.isPresent());
        assertEquals(trainee, result.get());
        verify(traineeStorage).findById(DEFAULT_TRAINEE_ID);
    }

    @Test
    void shouldFindAllFromCorrectNamespaceStorage() {
        Trainee trainee = buildTraineeWithId(DEFAULT_TRAINEE_ID);
        List<Trainee> trainees = List.of(trainee);

        when(traineeStorage.findAll()).thenReturn(trainees);

        List<Trainee> result = storage.findAll(Namespace.TRAINEE);

        assertEquals(1, result.size());
        assertEquals(trainee, result.get(0));
        verify(traineeStorage).findAll();
    }

    @Test
    void shouldDeleteFromCorrectNamespaceStorage() {
        when(traineeStorage.delete(DEFAULT_TRAINEE_ID)).thenReturn(true);

        boolean result = storage.delete(Namespace.TRAINEE, DEFAULT_TRAINEE_ID);

        assertTrue(result);
        verify(traineeStorage).delete(DEFAULT_TRAINEE_ID);
    }

    @Test
    void shouldReturnEmptyOptionalWhenEntityNotFoundInNamespace() {
        when(traineeStorage.findById(NON_EXISTENT_TRAINEE_ID)).thenReturn(Optional.empty());

        Optional<Trainee> result = storage.findById(Namespace.TRAINEE, NON_EXISTENT_TRAINEE_ID);

        assertFalse(result.isPresent());
        verify(traineeStorage).findById(NON_EXISTENT_TRAINEE_ID);
    }

    @Test
    void shouldReturnFalseWhenDeletingNonExistentEntityFromNamespace() {
        when(traineeStorage.delete(NON_EXISTENT_TRAINEE_ID)).thenReturn(false);

        boolean result = storage.delete(Namespace.TRAINEE, NON_EXISTENT_TRAINEE_ID);

        assertFalse(result);
        verify(traineeStorage).delete(NON_EXISTENT_TRAINEE_ID);
    }

}
