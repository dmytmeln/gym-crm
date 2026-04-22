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
class StorageInitializerNullPathTest {

    @Autowired
    private Storage storage;

    @Test
    void shouldHandleNullPathsGracefullyWithoutLoadingData() {
        List<Trainee> traineesResult = storage.findAll(Namespace.TRAINEE);
        List<Training> trainingsResult = storage.findAll(Namespace.TRAINING);
        List<Trainer> trainersResult = storage.findAll(Namespace.TRAINER);

        assertNotNull(traineesResult);
        assertNotNull(trainingsResult);
        assertNotNull(trainersResult);
        assertTrue(traineesResult.isEmpty());
        assertTrue(trainingsResult.isEmpty());
        assertTrue(trainersResult.isEmpty());
    }

}
