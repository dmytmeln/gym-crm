package org.gymcrm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringJUnitConfig(GymCrmApplication.class)
public class GymCrmApplicationTest {

    @Autowired
    private GymCrmApplication gymCrmApplication;

    @Test
    public void shouldInitializeGymCrmApplicationInSpringContext() {
        assertNotNull(gymCrmApplication, "The Spring Context should have initialized GymCrmApplication");
    }

}
