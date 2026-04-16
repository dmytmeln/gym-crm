package com.gym.crm.storage.init;

import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
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

import java.util.List;

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
        loadTraineeData();
        loadTrainerData();
        loadTrainingData();
    }

    private void loadTraineeData() {
        if (traineeFilePath == null) {
            return;
        }

        List<TraineeCsvDto> dtos = csvParser.parseCsv(traineeFilePath, TraineeCsvDto.class);

        for (TraineeCsvDto dto : dtos) {
            Trainee trainee = mapper.toTrainee(dto);
            storage.save(Namespace.TRAINEE, trainee);
        }
    }

    private void loadTrainerData() {
        if (trainerFilePath == null) {
            return;
        }

        List<TrainerCsvDto> dtos = csvParser.parseCsv(trainerFilePath, TrainerCsvDto.class);

        for (TrainerCsvDto dto : dtos) {
            Trainer trainer = mapper.toTrainer(dto);
            storage.save(Namespace.TRAINER, trainer);
        }
    }

    private void loadTrainingData() {
        if (trainingFilePath == null) {
            return;
        }

        List<TrainingCsvDto> dtos = csvParser.parseCsv(trainingFilePath, TrainingCsvDto.class);

        for (TrainingCsvDto dto : dtos) {
            Training training = mapper.toTraining(dto);
            storage.save(Namespace.TRAINING, training);
        }
    }

}
