package com.gym.crm.storage.csv.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TraineeCsvDto {

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
    private String address;

    @CsvBindByName(required = true)
    @CsvDate("yyyy-MM-dd")
    private LocalDate dateOfBirth;

}
