package com.gym.crm.storage.init;

import com.gym.crm.storage.Namespace;
import com.gym.crm.storage.Storage;
import com.gym.crm.storage.csv.CsvEntityMapper;
import com.gym.crm.storage.csv.CsvParser;
import com.gym.crm.storage.csv.dto.TraineeCsvDto;
import com.gym.crm.storage.csv.dto.TrainerCsvDto;
import com.gym.crm.storage.csv.dto.TrainingCsvDto;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.function.Function;

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
        loadData(traineeFilePath, TraineeCsvDto.class, mapper::toTrainee, Namespace.TRAINEE);
        loadData(trainerFilePath, TrainerCsvDto.class, mapper::toTrainer, Namespace.TRAINER);
        loadData(trainingFilePath, TrainingCsvDto.class, mapper::toTraining, Namespace.TRAINING);
    }

    private <D, E> void loadData(String filePath, Class<D> dtoClass, Function<D, E> mapperFunction, Namespace<E> namespace) {
        if (filePath == null) {
            return;
        }

        csvParser.parseCsv(filePath, dtoClass).stream()
                .map(mapperFunction)
                .forEach(entity -> storage.save(namespace, entity));
    }

}
