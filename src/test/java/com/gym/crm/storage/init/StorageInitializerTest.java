package com.gym.crm.storage.init;

import com.gym.crm.GymCrmApplication;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import com.gym.crm.storage.Namespace;
import com.gym.crm.storage.Storage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static com.gym.crm.factory.TraineeTestFactory.trainee;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(GymCrmApplication.class)
@TestPropertySource(properties = {
        "storage.init.trainee=classpath:test-data/trainee.csv",
        "storage.init.trainer=classpath:test-data/trainer.csv",
        "storage.init.training=classpath:test-data/training.csv"
})
public class StorageInitializerTest {

    @Autowired
    private Storage storage;

    @Test
    public void shouldLoadTraineesFromCsv() {
        List<Trainee> trainees = storage.findAll(Namespace.TRAINEE);
        Trainee firstTrainee = trainees.stream()
                .filter(t -> "john.doe".equals(t.getUsername()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Expected trainee with username 'john.doe' not found"));

        assertNotNull(trainees);
        assertFalse(trainees.isEmpty());
        assertEquals("John", firstTrainee.getFirstName());
        assertEquals("Doe", firstTrainee.getLastName());
        assertTrue(firstTrainee.isActive());
        assertNotNull(firstTrainee.getUserId());
        assertEquals("123 Main St", firstTrainee.getAddress());
        assertNotNull(firstTrainee.getDateOfBirth());
    }

    @Test
    public void shouldLoadTrainersFromCsv() {
        List<Trainer> trainers = storage.findAll(Namespace.TRAINER);
        Trainer firstTrainer = trainers.stream()
                .filter(t -> "mike.trainer".equals(t.getUsername()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Expected trainer with username 'mike.trainer' not found"));

        assertNotNull(trainers);
        assertFalse(trainers.isEmpty());
        assertNotNull(firstTrainer.getSpecialization());
        assertEquals("CARDIO", firstTrainer.getSpecialization().getTrainingTypeName());
        assertNotNull(firstTrainer.getUserId());
    }

    @Test
    public void shouldLoadTrainingsFromCsv() {
        List<Training> trainings = storage.findAll(Namespace.TRAINING);

        assertNotNull(trainings);
        assertFalse(trainings.isEmpty());
        Training firstTraining = trainings.get(0);
        assertNotNull(firstTraining.getId());
        assertNotNull(firstTraining.getTraineeId());
        assertNotNull(firstTraining.getTrainerId());
        assertNotNull(firstTraining.getTrainingType());
        assertNotNull(firstTraining.getTrainingName());
    }

    @Test
    public void shouldGenerateSequentialIdsAfterCsvLoad() {
        List<Trainee> loadedTrainees = storage.findAll(Namespace.TRAINEE);
        long maxLoadedId = loadedTrainees.stream()
                .mapToLong(Trainee::getUserId)
                .max()
                .orElseThrow(() -> new AssertionError("No trainees loaded from CSV"));
        Trainee newTrainee = trainee();

        Trainee saved = storage.save(Namespace.TRAINEE, newTrainee);

        assertTrue(saved.getUserId() > maxLoadedId);
    }

}
