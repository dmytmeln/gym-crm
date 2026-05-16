package com.gym.crm.dao;

import com.gym.crm.entity.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerDao {

    Trainer save(Trainer trainer);

    Optional<Trainer> findById(Long trainerId);

    Optional<Trainer> findByUsername(String username);

    List<Trainer> findAll();

    Trainer update(Trainer trainer);

}
