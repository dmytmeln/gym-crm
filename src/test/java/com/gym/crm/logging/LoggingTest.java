package com.gym.crm.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.gym.crm.storage.Storage;
import com.gym.crm.storage.csv.CsvEntityMapper;
import com.gym.crm.storage.csv.CsvParser;
import com.gym.crm.storage.csv.dto.TraineeCsvDto;
import com.gym.crm.storage.init.StorageInitializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoggingTest {

    @Mock
    private Storage storage;

    @Mock
    private CsvParser parser;

    @Mock
    private CsvEntityMapper mapper;

    @InjectMocks
    private StorageInitializer storageInitializer;

    private ListAppender<ILoggingEvent> listAppender;

    private Logger logger;

    @BeforeEach
    void setUp() {
        logger = (Logger) LoggerFactory.getLogger(StorageInitializer.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(listAppender);
    }

    @Test
    void shouldLogInfoWhenInitializationStarts() {
        storageInitializer.initializeStorage();

        assertThat(listAppender.list)
                .extracting(ILoggingEvent::getLevel)
                .contains(Level.INFO);
    }

    @Test
    void shouldLogErrorWhenDataLoadingFails() {
        String filePath = "invalid/path.csv";
        storageInitializer.setTraineeFilePath(filePath);

        when(parser.parseCsv(eq(filePath), eq(TraineeCsvDto.class))).thenThrow(new RuntimeException("Parsing failed"));

        storageInitializer.initializeStorage();

        assertThat(listAppender.list)
                .extracting(ILoggingEvent::getLevel)
                .contains(Level.ERROR);
    }

}
