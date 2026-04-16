package com.gym.crm.storage.csv.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TrainingCsvDto {

    @CsvBindByName(required = true)
    private Long traineeId;

    @CsvBindByName(required = true)
    private Long trainerId;

    @CsvBindByName(required = true)
    private String trainingName;

    @CsvBindByName(required = true)
    private String trainingTypeName;

    @CsvBindByName(required = true)
    private Integer trainingDuration;

    @CsvBindByName(required = true)
    @CsvDate("yyyy-MM-dd")
    private LocalDate trainingDate;

}
