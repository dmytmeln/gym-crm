package com.gym.crm.dao.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.model.Trainee;
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
import static com.gym.crm.factory.TraineeTestFactory.buildTrainee;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryTraineeDaoTest {

    private TraineeDao dao;

    @BeforeEach
    void setUp() {
        TraineeNamespaceStorage traineeNamespaceStorage = new TraineeNamespaceStorage();
        TrainerNamespaceStorage trainerNamespaceStorage = new TrainerNamespaceStorage();
        TrainingNamespaceStorage trainingNamespaceStorage = new TrainingNamespaceStorage();

        InMemoryStorage storage = new InMemoryStorage();
        storage.setNamespaceStorages(List.of(
                traineeNamespaceStorage,
                trainerNamespaceStorage,
                trainingNamespaceStorage));

        InMemoryTraineeDao implementation = new InMemoryTraineeDao();
        implementation.setStorage(storage);
        dao = implementation;
    }

    @Test
    void shouldCreateTraineeWithGeneratedId() {
        Trainee trainee = buildTrainee();

        Trainee actual = dao.create(trainee);

        assertNotNull(actual);
        assertNotNull(actual.getUserId());
        assertTrue(actual.getUserId() > 0, "ID must be positive");
        assertEquals(DEFAULT_USERNAME, actual.getUsername());
        assertEquals(DEFAULT_FIRST_NAME, actual.getFirstName());
        assertEquals(DEFAULT_LAST_NAME, actual.getLastName());
    }

    @Test
    void shouldGenerateSequentialIds() {
        Trainee trainee1 = buildTrainee();
        Trainee trainee2 = buildTrainee();

        Trainee actual1 = dao.create(trainee1);
        Trainee actual2 = dao.create(trainee2);

        assertTrue(actual2.getUserId() > actual1.getUserId());
    }

    @Test
    void shouldThrowNullPointerExceptionWhenCreatingNullTrainee() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> dao.create(null));

        assertEquals("Trainee cannot be null", exception.getMessage());
    }

    @Test
    void shouldFindTraineeByIdAfterCreation() {
        Trainee trainee = buildTrainee();
        Trainee expected = dao.create(trainee);

        Optional<Trainee> actual = dao.findById(expected.getUserId());

        assertTrue(actual.isPresent());
        assertEquals(expected.getUserId(), actual.get().getUserId());
        assertEquals(DEFAULT_USERNAME, actual.get().getUsername());
    }

    @Test
    void shouldReturnEmptyOptionalWhenTraineeNotFound() {
        Optional<Trainee> actual = dao.findById(NON_EXISTENT_TRAINEE_ID);

        assertFalse(actual.isPresent());
    }

    @Test
    void shouldThrowNullPointerExceptionWhenFindingByNullId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> dao.findById(null));

        assertEquals("ID cannot be null", exception.getMessage());
    }

    @Test
    void shouldFindAllTrainees() {
        Trainee trainee1 = buildTrainee();
        Trainee trainee2 = buildTrainee();
        dao.create(trainee1);
        dao.create(trainee2);

        List<Trainee> actual = dao.findAll();

        assertNotNull(actual);
        int expectedSize = 2;
        assertEquals(expectedSize, actual.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoTrainees() {
        List<Trainee> actual = dao.findAll();

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    void shouldUpdateExistingTrainee() {
        Trainee trainee = buildTrainee();
        Trainee created = dao.create(trainee);
        String expectedFirstName = "Johnny";
        Trainee updated = buildTrainee(created.getUserId(), DEFAULT_USERNAME, expectedFirstName, DEFAULT_LAST_NAME);

        Trainee actual = dao.update(updated);

        assertEquals(created.getUserId(), actual.getUserId());
        assertEquals(expectedFirstName, actual.getFirstName());
        assertEquals(DEFAULT_USERNAME, actual.getUsername());

        Optional<Trainee> found = dao.findById(created.getUserId());
        assertTrue(found.isPresent());
        assertEquals(expectedFirstName, found.get().getFirstName());
        assertEquals(DEFAULT_USERNAME, found.get().getUsername());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenUpdatingWithoutId() {
        Trainee trainee = buildTrainee();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> dao.update(trainee));

        assertEquals("Cannot update entity without ID", exception.getMessage());
    }

    @Test
    void shouldThrowNullPointerExceptionWhenUpdatingNullTrainee() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> dao.update(null));

        assertEquals("Trainee cannot be null", exception.getMessage());
    }

    @Test
    void shouldDeleteExistingTrainee() {
        Trainee trainee = buildTrainee();
        Trainee created = dao.create(trainee);

        boolean actual = dao.delete(created.getUserId());

        assertTrue(actual);
        assertFalse(dao.findById(created.getUserId()).isPresent());
    }

    @Test
    void shouldReturnFalseWhenDeletingNonExistentTrainee() {
        boolean actual = dao.delete(NON_EXISTENT_TRAINEE_ID);

        assertFalse(actual);
    }

    @Test
    void shouldThrowNullPointerExceptionWhenDeletingWithNullId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> dao.delete(null));

        assertEquals("ID cannot be null", exception.getMessage());
    }

}
