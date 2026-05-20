package com.gym.crm.dao;

import com.gym.crm.entity.TrainingType;

import java.util.List;
import java.util.Optional;

public interface TrainingTypeDao {

    Optional<TrainingType> findByName(String name);

    List<TrainingType> findAll();

}
