package com.gym.crm;

import com.gym.crm.config.BaseDbIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringJUnitConfig(GymCrmApplication.class)
class GymCrmApplicationTest extends BaseDbIntegrationTest {

    @Autowired
    private GymCrmApplication app;

    @Test
    void shouldInitializeGymCrmApplicationInSpringContext() {
        assertNotNull(app, "The Spring Context should have initialized GymCrmApplication");
    }

}
