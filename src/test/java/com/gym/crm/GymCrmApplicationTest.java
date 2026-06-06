package com.gym.crm;

import com.gym.crm.config.MySqlContainerTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class GymCrmApplicationTest {

    @Autowired
    private GymCrmApplication app;

    @DynamicPropertySource
    static void setMySqlProperties(DynamicPropertyRegistry registry) {
        MySqlContainerTestConfig.setMySqlContainerProperties(registry);
    }

    @Test
    void shouldInitializeGymCrmApplicationInSpringContext() {
        assertNotNull(app, "The Spring Context should have initialized GymCrmApplication");
    }

}
