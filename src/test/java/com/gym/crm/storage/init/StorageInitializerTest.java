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

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitConfig(GymCrmApplication.class)
@TestPropertySource(properties = {
        "storage.init.trainee=classpath:test-data/trainee.csv",
        "storage.init.trainer=classpath:test-data/trainer.csv",
        "storage.init.training=classpath:test-data/training.csv"
})
class StorageInitializerTest {

    @Autowired
    private Storage storage;

    @Test
    void shouldLoadTraineesFromCsv() {
        List<Trainee> result = storage.findAll(Namespace.TRAINEE);
        result.sort(Comparator.comparing(Trainee::getUsername));

        assertEquals(3, result.size());
        assertEquals("bob.wilson", result.get(0).getUsername());
        assertEquals("liam.miller", result.get(1).getUsername());
        assertEquals("sophia.wilson", result.get(2).getUsername());
    }

    @Test
    void shouldLoadTrainersFromCsv() {
        List<Trainer> result = storage.findAll(Namespace.TRAINER);
        result.sort(Comparator.comparing(Trainer::getUsername));

        assertEquals(3, result.size());
        assertEquals("alex.morgan", result.get(0).getUsername());
        assertEquals("marcus.stone", result.get(1).getUsername());
        assertEquals("sarah.adams", result.get(2).getUsername());
    }

    @Test
    void shouldLoadTrainingsFromCsv() {
        List<Training> result = storage.findAll(Namespace.TRAINING);
        result.sort(Comparator.comparing(Training::getTrainingName));

        assertEquals(4, result.size());
        assertEquals("Evening Run", result.get(0).getTrainingName());
        assertEquals("Morning Cardio Session", result.get(1).getTrainingName());
        assertEquals("Strength Training", result.get(2).getTrainingName());
        assertEquals("Yoga Class", result.get(3).getTrainingName());
    }

}
