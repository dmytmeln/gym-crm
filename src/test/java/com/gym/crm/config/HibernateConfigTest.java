package com.gym.crm.config;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig({HibernateConfig.class, DatasourceConfig.class})
@TestPropertySource(locations = "classpath:application-test.properties")
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

}
