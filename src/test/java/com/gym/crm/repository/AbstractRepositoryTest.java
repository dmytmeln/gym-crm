package com.gym.crm.repository;

import com.gym.crm.config.BaseDbIntegrationTest;
import com.gym.crm.config.DaoTestConfig;
import com.gym.crm.repository.specification.TraineeTrainingCriteriaBuilder;
import com.gym.crm.repository.specification.TrainerTrainingCriteriaBuilder;
import com.gym.crm.test.helper.TestDbClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Import({DaoTestConfig.class, TraineeTrainingCriteriaBuilder.class, TrainerTrainingCriteriaBuilder.class})
@ActiveProfiles("test")
@Sql(scripts = {"classpath:datasets/cleanup-all.sql", "classpath:datasets/seed-data.sql"})
abstract class AbstractRepositoryTest<T> extends BaseDbIntegrationTest {

    @Autowired
    protected TestDbClient testDbClient;

    @Autowired
    protected TraineeTrainingCriteriaBuilder traineeCriteriaBuilder;

    @Autowired
    protected TrainerTrainingCriteriaBuilder trainerCriteriaBuilder;

    @Autowired
    protected T repository;

    @Test
    void shouldStartContainer() {
        assertTrue(MY_SQL_CONTAINER.isRunning());
    }

}
