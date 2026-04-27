package com.gym.crm.storage.csv;

import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import com.gym.crm.storage.csv.dto.TraineeCsvDto;
import com.gym.crm.storage.csv.dto.TrainerCsvDto;
import com.gym.crm.storage.csv.dto.TrainingCsvDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING, imports = TrainingType.class)
public interface CsvEntityMapper {

    @Mapping(target = "userId", ignore = true)
    Trainee toTrainee(TraineeCsvDto dto);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "specialization", expression = "java(new TrainingType(dto.getSpecializationType()))")
    Trainer toTrainer(TrainerCsvDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainingType", expression = "java(new TrainingType(dto.getTrainingTypeName()))")
    Training toTraining(TrainingCsvDto dto);

}
