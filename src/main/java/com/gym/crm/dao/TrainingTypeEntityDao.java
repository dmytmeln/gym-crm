package com.gym.crm.dao;

import com.gym.crm.entity.TrainingType;

import java.util.Optional;

public interface TrainingTypeEntityDao {

    Optional<TrainingType> findById(Long id);

    Optional<TrainingType> findByName(String name);

}
