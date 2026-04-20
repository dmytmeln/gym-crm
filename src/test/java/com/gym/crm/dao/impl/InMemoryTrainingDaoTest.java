package com.gym.crm.dao.impl;

import com.gym.crm.dao.TrainingDao;
import com.gym.crm.entity.Training;
import com.gym.crm.storage.impl.InMemoryStorage;
import com.gym.crm.storage.impl.TraineeNamespaceStorage;
import com.gym.crm.storage.impl.TrainerNamespaceStorage;
import com.gym.crm.storage.impl.TrainingNamespaceStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.gym.crm.factory.TrainingTestFactory.DEFAULT_TRAINEE_ID;
import static com.gym.crm.factory.TrainingTestFactory.DEFAULT_TRAINER_ID;
import static com.gym.crm.factory.TrainingTestFactory.DEFAULT_TRAINING_NAME;
import static com.gym.crm.factory.TrainingTestFactory.NON_EXISTENT_TRAINING_ID;
import static com.gym.crm.factory.TrainingTestFactory.training;
import static com.gym.crm.factory.TrainingTestFactory.trainingWithoutId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryTrainingDaoTest {

    private TrainingDao dao;

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

        InMemoryTrainingDao implementation = new InMemoryTrainingDao();
        implementation.setStorage(storage);
        dao = implementation;

        trainingNamespaceStorage.clear();
    }

    @Test
    public void shouldCreateTrainingWithGeneratedId() {
        Training training = training();

        Training result = dao.create(training);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertTrue(result.getId() > 0, "ID must be positive");
        assertEquals(DEFAULT_TRAINEE_ID, result.getTraineeId());
        assertEquals(DEFAULT_TRAINER_ID, result.getTrainerId());
        assertEquals(DEFAULT_TRAINING_NAME, result.getTrainingName());
    }

    @Test
    public void shouldGenerateSequentialIds() {
        Training training1 = trainingWithoutId(10L, 20L);
        Training training2 = trainingWithoutId(11L, 21L);

        Training result1 = dao.create(training1);
        Training result2 = dao.create(training2);

        assertTrue(result2.getId() > result1.getId());
    }

    @Test
    public void shouldThrowNullPointerExceptionWhenCreatingNullTraining() {
        assertThrows(NullPointerException.class, () -> dao.create(null));
    }

    @Test
    public void shouldFindTrainingByIdAfterCreation() {
        Training training = training();
        Training created = dao.create(training);

        Optional<Training> result = dao.findById(created.getId());

        assertTrue(result.isPresent());
        assertEquals(created.getId(), result.get().getId());
        assertEquals(DEFAULT_TRAINING_NAME, result.get().getTrainingName());
    }

    @Test
    public void shouldReturnEmptyOptionalWhenTrainingNotFound() {
        Optional<Training> result = dao.findById(NON_EXISTENT_TRAINING_ID);

        assertFalse(result.isPresent());
    }

    @Test
    public void shouldThrowNullPointerExceptionWhenFindingByNullId() {
        assertThrows(NullPointerException.class, () -> dao.findById(null));
    }

    @Test
    public void shouldFindAllTrainings() {
        Training training1 = trainingWithoutId(10L, 20L);
        Training training2 = trainingWithoutId(11L, 21L);
        dao.create(training1);
        dao.create(training2);

        List<Training> result = dao.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void shouldReturnEmptyListWhenNoTrainings() {
        List<Training> result = dao.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldUpdateExistingTraining() {
        Training training = training();
        Training created = dao.create(training);
        Training updated = training(created.getId(), "Evening Workout");

        Training result = dao.update(updated);

        assertEquals(created.getId(), result.getId());
        assertEquals("Evening Workout", result.getTrainingName());

        Optional<Training> found = dao.findById(created.getId());
        assertTrue(found.isPresent());
        assertEquals("Evening Workout", found.get().getTrainingName());
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenUpdatingWithoutId() {
        Training training = training();

        assertThrows(IllegalArgumentException.class, () -> dao.update(training));
    }

    @Test
    public void shouldThrowNullPointerExceptionWhenUpdatingNullTraining() {
        assertThrows(NullPointerException.class, () -> dao.update(null));
    }

    @Test
    public void shouldDeleteExistingTraining() {
        Training training = training();
        Training created = dao.create(training);

        boolean result = dao.delete(created.getId());

        assertTrue(result);
        assertFalse(dao.findById(created.getId()).isPresent());
    }

    @Test
    public void shouldReturnFalseWhenDeletingNonExistentTraining() {
        boolean result = dao.delete(NON_EXISTENT_TRAINING_ID);

        assertFalse(result);
    }

    @Test
    public void shouldThrowNullPointerExceptionWhenDeletingWithNullId() {
        assertThrows(NullPointerException.class, () -> dao.delete(null));
    }

}
