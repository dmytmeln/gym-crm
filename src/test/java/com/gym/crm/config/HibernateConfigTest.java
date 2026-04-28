package com.gym.crm.config;

import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import com.gym.crm.entity.TrainingType;
import com.gym.crm.entity.User;
import com.gym.crm.model.Trainee;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig({HibernateConfig.class, DatasourceConfig.class})
@TestPropertySource(locations = "classpath:application-test.yml", factory = YamlPropertySourceFactory.class)
class HibernateConfigTest {

    @Autowired
    private SessionFactory factory;

    @Test
    void shouldCreateSessionFactory() {
        assertThat(factory).isNotNull();
    }

    @Test
    void sessionFactoryShouldBeOpen() {
        assertThat(factory.isOpen()).isTrue();
    }

    @Test
    void shouldOpenSession() {
        try (Session session = factory.openSession()) {
            assertThat(session.isOpen()).isTrue();
        }
    }

    @Test
    void shouldConnectSessionToDb() {
        try (Session session = factory.openSession()) {
            assertThat(session.isConnected()).isTrue();
        }
    }

    @Test
    void shouldExecuteQueryOnConnectedSession() {
        try (Session session = factory.openSession()) {
            session.doWork(connection -> assertThat(connection.isValid(1)).isTrue());
        }
    }

    @Test
    void shouldHaveAllEntitiesInMetamodel() {
        assertThat(factory.getMetamodel().getEntities())
                .extracting(e -> e.getJavaType().getName())
                .containsExactlyInAnyOrder(
                        Trainee.class.getName(), Trainer.class.getName(), Training.class.getName(), TrainingType.class.getName(),
                        User.class.getName());
    }

}
