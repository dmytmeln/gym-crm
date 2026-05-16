package com.gym.crm.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.common.ProfileCredentialGenerator;
import com.gym.crm.service.impl.TraineeServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoggingTest {

    @Mock
    private TraineeDao dao;

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private ProfileCredentialGenerator generator;

    private TraineeService service;

    private ListAppender<ILoggingEvent> listAppender;

    private Logger logger;

    @BeforeEach
    void setUp() {
        TraineeServiceImpl implementation = new TraineeServiceImpl();
        implementation.setTraineeDao(dao);
        implementation.setCredentialGenerator(generator);
        service = implementation;

        logger = (Logger) LoggerFactory.getLogger(TraineeServiceImpl.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(listAppender);
    }

    @Test
    void shouldLogInfoWhenDeleteSucceeds() {
        when(dao.deleteByUsername(DEFAULT_USERNAME)).thenReturn(true);

        service.deleteTraineeByUsername(DEFAULT_USERNAME);

        assertThat(listAppender.list)
                .extracting(ILoggingEvent::getLevel)
                .contains(Level.INFO);
    }

    @Test
    void shouldLogWarnWhenNotDeleted() {
        when(dao.deleteByUsername(DEFAULT_USERNAME)).thenReturn(false);

        service.deleteTraineeByUsername(DEFAULT_USERNAME);

        assertThat(listAppender.list)
                .extracting(ILoggingEvent::getLevel)
                .contains(Level.WARN);
    }

}
