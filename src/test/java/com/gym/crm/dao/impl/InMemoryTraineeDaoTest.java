package com.gym.crm.dao.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.storage.impl.InMemoryStorage;
import com.gym.crm.storage.impl.TraineeNamespaceStorage;
import com.gym.crm.storage.impl.TrainerNamespaceStorage;
import com.gym.crm.storage.impl.TrainingNamespaceStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_FIRST_NAME;
import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_LAST_NAME;
import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_USERNAME;
import static com.gym.crm.factory.TraineeTestFactory.NON_EXISTENT_TRAINEE_ID;
import static com.gym.crm.factory.TraineeTestFactory.trainee;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryTraineeDaoTest {

    private TraineeDao dao;

    @BeforeEach
    public void setUp() {
        TraineeNamespaceStorage traineeNamespaceStorage = new TraineeNamespaceStorage();
        TrainerNamespaceStorage trainerNamespaceStorage = new TrainerNamespaceStorage();
        TrainingNamespaceStorage trainingNamespaceStorage = new TrainingNamespaceStorage();

        InMemoryStorage storage = new InMemoryStorage();
        storage.setNamespaceStorages(List.of(
                traineeNamespaceStorage,
                trainerNamespaceStorage,
                trainingNamespaceStorage
        ));

        InMemoryTraineeDao implementation = new InMemoryTraineeDao();
        implementation.setStorage(storage);
        dao = implementation;

        traineeNamespaceStorage.clear();
    }

    @Test
    public void shouldCreateTraineeWithGeneratedId() {
        Trainee trainee = trainee();

        Trainee result = dao.create(trainee);

        assertNotNull(result);
        assertNotNull(result.getUserId());
        assertTrue(result.getUserId() > 0, "ID must be positive");
        assertEquals(DEFAULT_USERNAME, result.getUsername());
        assertEquals(DEFAULT_FIRST_NAME, result.getFirstName());
        assertEquals(DEFAULT_LAST_NAME, result.getLastName());
    }

    @Test
    public void shouldGenerateSequentialIds() {
        Trainee trainee1 = trainee();
        Trainee trainee2 = trainee();

        Trainee result1 = dao.create(trainee1);
        Trainee result2 = dao.create(trainee2);

        assertTrue(result2.getUserId() > result1.getUserId());
    }

    @Test
    public void shouldThrowNullPointerExceptionWhenCreatingNullTrainee() {
        assertThrows(NullPointerException.class, () -> dao.create(null));
    }

    @Test
    public void shouldFindTraineeByIdAfterCreation() {
        Trainee trainee = trainee();
        Trainee created = dao.create(trainee);

        Optional<Trainee> result = dao.findById(created.getUserId());

        assertTrue(result.isPresent());
        assertEquals(created.getUserId(), result.get().getUserId());
        assertEquals(DEFAULT_USERNAME, result.get().getUsername());
    }

    @Test
    public void shouldReturnEmptyOptionalWhenTraineeNotFound() {
        Optional<Trainee> result = dao.findById(NON_EXISTENT_TRAINEE_ID);

        assertFalse(result.isPresent());
    }

    @Test
    public void shouldThrowNullPointerExceptionWhenFindingByNullId() {
        assertThrows(NullPointerException.class, () -> dao.findById(null));
    }

    @Test
    public void shouldFindAllTrainees() {
        Trainee trainee1 = trainee();
        Trainee trainee2 = trainee();
        dao.create(trainee1);
        dao.create(trainee2);

        List<Trainee> result = dao.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void shouldReturnEmptyListWhenNoTrainees() {
        List<Trainee> result = dao.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldUpdateExistingTrainee() {
        Trainee trainee = trainee();
        Trainee created = dao.create(trainee);
        Trainee updated = trainee(created.getUserId(), DEFAULT_USERNAME, "Johnny", DEFAULT_LAST_NAME);

        Trainee result = dao.update(updated);

        assertEquals(created.getUserId(), result.getUserId());
        assertEquals("Johnny", result.getFirstName());
        assertEquals(DEFAULT_USERNAME, result.getUsername());

        Optional<Trainee> found = dao.findById(created.getUserId());
        assertTrue(found.isPresent());
        assertEquals("Johnny", found.get().getFirstName());
        assertEquals(DEFAULT_USERNAME, found.get().getUsername());
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenUpdatingWithoutId() {
        Trainee trainee = trainee();

        assertThrows(IllegalArgumentException.class, () -> dao.update(trainee));
    }

    @Test
    public void shouldThrowNullPointerExceptionWhenUpdatingNullTrainee() {
        assertThrows(NullPointerException.class, () -> dao.update(null));
    }

    @Test
    public void shouldDeleteExistingTrainee() {
        Trainee trainee = trainee();
        Trainee created = dao.create(trainee);

        boolean result = dao.delete(created.getUserId());

        assertTrue(result);
        assertFalse(dao.findById(created.getUserId()).isPresent());
    }

    @Test
    public void shouldReturnFalseWhenDeletingNonExistentTrainee() {
        boolean result = dao.delete(NON_EXISTENT_TRAINEE_ID);

        assertFalse(result);
    }

    @Test
    public void shouldThrowNullPointerExceptionWhenDeletingWithNullId() {
        assertThrows(NullPointerException.class, () -> dao.delete(null));
    }

}
