package com.gym.crm.repository;

import com.gym.crm.config.DaoTestConfig;
import com.gym.crm.config.DbIntegrationTest;
import com.gym.crm.config.TestDataset;
import com.gym.crm.repository.specification.TraineeTrainingCriteriaBuilder;
import com.gym.crm.repository.specification.TrainerTrainingCriteriaBuilder;
import com.gym.crm.test.helper.TestDbClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

@DataJpaTest
@Transactional(propagation = NOT_SUPPORTED)
@Import({DaoTestConfig.class, TraineeTrainingCriteriaBuilder.class, TrainerTrainingCriteriaBuilder.class})
@ActiveProfiles("test")
@TestDataset
abstract class AbstractRepositoryTest<T> {

    @Autowired
    protected TestDbClient testDbClient;

    @Autowired
    protected TraineeTrainingCriteriaBuilder traineeCriteriaBuilder;

    @Autowired
    protected TrainerTrainingCriteriaBuilder trainerCriteriaBuilder;

    @Autowired
    protected T repository;

    @DynamicPropertySource
    static void setMySqlProperties(DynamicPropertyRegistry registry) {
        DbIntegrationTest.setMySqlContainerProperties(registry);
    }

}
