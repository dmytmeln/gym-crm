package com.gym.crm.storage.csv.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainerCsvDto {

    @CsvBindByName(required = true)
    private String username;

    @CsvBindByName(required = true)
    private String firstName;

    @CsvBindByName(required = true)
    private String lastName;

    @CsvBindByName(required = true)
    private String password;

    @CsvBindByName(required = true)
    private Boolean isActive;

    @CsvBindByName(required = true)
    private String specializationType;

}
