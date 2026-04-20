package com.gym.crm.storage.init;

import com.gym.crm.GymCrmApplication;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import com.gym.crm.storage.Namespace;
import com.gym.crm.storage.Storage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(GymCrmApplication.class)
public class StorageInitializerNullPathTest {

    @Autowired
    private Storage storage;

    @Test
    public void shouldHandleNullPathsGracefullyWithoutLoadingData() {
        List<Trainee> trainees = storage.findAll(Namespace.TRAINEE);
        List<Training> trainings = storage.findAll(Namespace.TRAINING);
        List<Trainer> trainers = storage.findAll(Namespace.TRAINER);

        assertNotNull(trainees);
        assertNotNull(trainings);
        assertNotNull(trainers);
        assertTrue(trainees.isEmpty());
        assertTrue(trainings.isEmpty());
        assertTrue(trainers.isEmpty());
    }

}
