package com.gym.crm.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.gym.crm.entity.Trainee;
import com.gym.crm.repository.TraineeRepository;
import com.gym.crm.repository.TrainerRepository;
import com.gym.crm.repository.TrainingRepository;
import com.gym.crm.repository.specification.TraineeTrainingCriteriaBuilder;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_USERNAME;
import static com.gym.crm.factory.TraineeTestFactory.buildTraineeWithUsername;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoggingTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private ProfileCredentialGenerator generator;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TraineeTrainingCriteriaBuilder trainingCriteriaBuilder;

    private TraineeService service;

    private ListAppender<ILoggingEvent> listAppender;

    private Logger logger;

    @BeforeEach
    void setUp() {
        service = new TraineeServiceImpl(traineeRepository,
                trainerRepository,
                trainingRepository,
                generator,
                passwordEncoder,
                trainingCriteriaBuilder);

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
        Trainee trainee = buildTraineeWithUsername(DEFAULT_USERNAME);

        when(traineeRepository.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(trainee));

        service.deleteTraineeByUsername(DEFAULT_USERNAME);

        assertThat(listAppender.list)
                .extracting(ILoggingEvent::getLevel)
                .contains(Level.INFO);
    }

    @Test
    void shouldLogWarnWhenNotDeleted() {
        when(traineeRepository.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.empty());

        service.deleteTraineeByUsername(DEFAULT_USERNAME);

        assertThat(listAppender.list)
                .extracting(ILoggingEvent::getLevel)
                .contains(Level.WARN);
    }

}
