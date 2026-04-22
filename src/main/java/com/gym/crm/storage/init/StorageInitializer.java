package com.gym.crm.storage.init;

import com.gym.crm.storage.Namespace;
import com.gym.crm.storage.Storage;
import com.gym.crm.storage.csv.CsvEntityMapper;
import com.gym.crm.storage.csv.CsvParser;
import com.gym.crm.storage.csv.dto.TraineeCsvDto;
import com.gym.crm.storage.csv.dto.TrainerCsvDto;
import com.gym.crm.storage.csv.dto.TrainingCsvDto;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Slf4j
@Component
public class StorageInitializer {

    private Storage storage;
    private CsvParser csvParser;
    private CsvEntityMapper mapper;
    private String traineeFilePath;
    private String trainerFilePath;
    private String trainingFilePath;

    @Autowired
    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    @Autowired
    public void setCsvParser(CsvParser csvParser) {
        this.csvParser = csvParser;
    }

    @Autowired
    public void setMapper(CsvEntityMapper mapper) {
        this.mapper = mapper;
    }

    @Value("${storage.init.trainee:#{null}}")
    public void setTraineeFilePath(String traineeFilePath) {
        this.traineeFilePath = traineeFilePath;
    }

    @Value("${storage.init.trainer:#{null}}")
    public void setTrainerFilePath(String trainerFilePath) {
        this.trainerFilePath = trainerFilePath;
    }

    @Value("${storage.init.training:#{null}}")
    public void setTrainingFilePath(String trainingFilePath) {
        this.trainingFilePath = trainingFilePath;
    }

    @PostConstruct
    public void initializeStorage() {
        log.info("Starting storage initialization...");
        loadData(traineeFilePath, TraineeCsvDto.class, mapper::toTrainee, Namespace.TRAINEE);
        loadData(trainerFilePath, TrainerCsvDto.class, mapper::toTrainer, Namespace.TRAINER);
        loadData(trainingFilePath, TrainingCsvDto.class, mapper::toTraining, Namespace.TRAINING);
        log.info("Storage initialization completed.");
    }

    private <D, E> void loadData(String filePath,
                                 Class<D> dtoClass,
                                 Function<D, E> mapperFunction,
                                 Namespace<E> namespace) {
        if (filePath == null) {
            log.warn("File path for namespace {} is not provided. Skipping initialization.", namespace);
            return;
        }

        try {
            List<D> dtos = csvParser.parseCsv(filePath, dtoClass);
            log.debug("Parsed {} records from {} for namespace {}.", dtos.size(), filePath, namespace);

            dtos.stream()
                    .map(mapperFunction)
                    .forEach(entity -> storage.save(namespace, entity));
        } catch (Exception e) {
            log.error("Failed to load data for namespace {} from file {}", namespace, filePath, e);
        }
    }

}
